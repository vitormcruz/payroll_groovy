package com.vmc.validationNotification

import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy

import java.lang.reflect.Method

/**
 * I am a NullObject for validated objects that dynamically "overrides" any object. I implement onBuildSucess and onBuildFailure accordinly and throw an
 * UnsupportedOperationException for every method call tha is not implemented on the Object class.
 */
class DynamicNullValidatedObject {

    private Validate validateObject
    private Class executionResultClass

    DynamicNullValidatedObject(Validate validateObject) {
        this.validateObject = validateObject
        executionResultClass = validateObject.executionResult.getClass()
    }

    /**
     * @see com.vmc.validationNotification.ObjectValidationNotificationExtensions#onBuildSucess(java.lang.Object, groovy.lang.Closure)
     */
    def onBuildSucess(Closure aSuccessClosure){
        return null
    }

    /**
     * @see com.vmc.validationNotification.ObjectValidationNotificationExtensions#onBuildFailure(java.lang.Object, groovy.lang.Closure)
     */
    def onBuildFailure(Closure aFailureClosure){
        return aFailureClosure(validateObject.errorsByContext)
    }

    def methodMissing(String name, Object args) {
        throw new UnsupportedOperationException("I am a NullObject for ${validateObject.executionResult.getClass()} and I only respond to onBuildFailure or onBuildSucess " +
                                                "messages, any other messa sent to myself is an error")
    }

    def asValidatedObjectProxy() {
        def proxy = Enhancer.create(executionResultClass, { Object obj, Method method, Object[] args, MethodProxy proxyMethod ->
            if (classInfoMessages().contains(method.name)) {
                return proxyMethod.invokeSuper(obj, args)
            }

            return delegate.invokeMethod(method.name, args)} as MethodInterceptor)

        /** Enhancer cannot intercep dynamic added objects, which gets hooked on the proxy as it would be done in any other object, so I must force delegation here. **/
        proxy.metaClass."onBuildSucess" = this.&onBuildSucess
        proxy.metaClass."onBuildFailure" = this.&onBuildFailure
        return proxy
    }

    public List<String> classInfoMessages() {
        ["setMetaClass", "getMetaClass", "getClass", "\$getStaticMetaClass"]
    }
}

