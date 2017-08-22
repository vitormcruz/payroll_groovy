package com.vmc.validationNotification

import com.vmc.validationNotification.api.ValidationResult
import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.ExceptionMethod
import net.bytebuddy.matcher.ElementMatcher

import static net.bytebuddy.matcher.ElementMatchers.named
import static net.bytebuddy.matcher.ElementMatchers.not

//TODO Add tests
class ValidationFail implements ValidationResult{

    private static final ElementMatcher UNUPORTED_NULL_OBJECT_METHODS_MATCHER = methodsToRespondEvenIfNullObject().collect { methodName -> not(named(methodName)) }
                                                                                                                 .inject { acc, val -> acc.and(val) }
    private static nullObjectByClass = [:]

    private nullObject
    private Validate validateObject

    static List<String> methodsToRespondEvenIfNullObject() {
        def methodsToRespond = ["setMetaClass", "getMetaClass", "getClass", "\$getStaticMetaClass"]
        methodsToRespond.addAll(Object.getDeclaredMethods().collect {it.name})
        methodsToRespond.addAll(GroovyObject.declaredMethods.name)
        return methodsToRespond
    }

    @Override
    void setValidateObject(Validate validateObject) {
        this.validateObject = validateObject
        this.nullObject = createNullObjectByteBuddy(validateObject)
    }

    @Override
    getExecutionResult() {
        return nullObject
    }

    def createNullObjectByteBuddy(Validate validateObject) {
        def classValidated = validateObject.getClassValidated()
        Class<?> nullObjectClass = nullObjectByClass.get(classValidated)
        if(nullObjectClass == null){
             nullObjectClass = new ByteBuddy().subclass(classValidated)
                                              .name(classValidated.getName() + "_NullObject")
                                              .method(UNUPORTED_NULL_OBJECT_METHODS_MATCHER).intercept(ExceptionMethod.throwing(UnsupportedOperationException,
                                                                                                                                "I am a NullObject and I cannot respond to " +
                                                                                                                                "this message."))
                                              .make()
                                              .load(Thread.currentThread().getContextClassLoader())
                                              .getLoaded()

            nullObjectByClass.put(classValidated, nullObjectClass)
        }

        def nullObjectInstance = nullObjectClass.newInstance()
        nullObjectInstance.metaClass."onBuildSuccess" = {Closure aSuccessClosure -> return delegate}
        nullObjectInstance.metaClass."onBuildFailure" = {Closure aFailureClosure ->
            aFailureClosure(validateObject.errorsByContext)
            return delegate
        }
        return nullObjectInstance
    }

    ElementMatcher nullObjectMethodsMatcher() {
        return UNUPORTED_NULL_OBJECT_METHODS_MATCHER
    }
}
