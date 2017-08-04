package com.vmc.validationNotification

import com.vmc.validationNotification.api.ValidationResult
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy

import java.lang.reflect.Method

//TODO Add tests
class ValidationFail implements ValidationResult{

    private nullObject
    private Validate validateObject

    @Override
    void setValidateObject(Validate validateObject) {
        this.validateObject = validateObject
        this.nullObject = createNullObject(validateObject)
    }

    @Override
    getExecutionResult() {
        return nullObject
    }

    /**
     * I create a NullObject for validated objects that dynamically "overrides" the result object from validateObject. I implement onBuildSucess and onBuildFailure accordinly
     * and throw an UnsupportedOperationException for every method call that is not implemented on the Object class.
     */
    def createNullObject(Validate validateObject) {
        def classToProxyAsNullObject = validateObject.executionResult.getClass()
        def proxy = Enhancer.create(classToProxyAsNullObject, { Object obj, Method method, Object[] args, MethodProxy proxyMethod ->
            if (methodsToRespondEvenIfNullObject().contains(method.name)) {
                return proxyMethod.invokeSuper(obj, args)
            }

            throw new UnsupportedOperationException("I am a NullObject of ${classToProxyAsNullObject}, and I cannot respond to the ${method.name} message.")

        } as MethodInterceptor)

        proxy.metaClass."onBuildSucess" = {Closure aSucessClosure -> return delegate}
        proxy.metaClass."onBuildFailure" = {Closure aFailureClosure ->
            aFailureClosure(validateObject.errorsByContext)
            return delegate
        }
        return proxy
    }

    //todo cahce this?
    public List<String> methodsToRespondEvenIfNullObject() {
        def methodsToRespond = ["setMetaClass", "getMetaClass", "getClass", "\$getStaticMetaClass"]
        methodsToRespond.addAll(Object.getDeclaredMethods().collect {it.name})
        methodsToRespond.addAll(GroovyObject.declaredMethods.name)
        return methodsToRespond
    }
}
