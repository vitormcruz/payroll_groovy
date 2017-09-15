package com.vmc.validationNotification


import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.ExceptionMethod
import net.bytebuddy.matcher.ElementMatcher

import static net.bytebuddy.matcher.ElementMatchers.named
import static net.bytebuddy.matcher.ElementMatchers.not

//TODO review failIfCantCreateNullObject
class GenericNullObjectBuilder {

    private static final ElementMatcher CORE_LANG_METHODS_MATCHER = coreLangMethods().collect { methodName -> not(named(methodName)) }
                                                                                     .inject { acc, val -> acc.and(val) }
    private static nullObjectByClass = [:]

    static <N> N newNullObjectOf(Class<N> aClass, errors) {
        return newInstanceWithErrors(getNullObjectClassOf(aClass), errors)
    }

    static Class<?> getNullObjectClassOf(Class<?> aClass) {
        Class<?> nullObjectClass = nullObjectByClass.get(aClass)
        if (nullObjectClass == null) {
            nullObjectClass = createNullObjectClassFor(aClass)
            nullObjectByClass.put(aClass, nullObjectClass)
        }

        return nullObjectClass
    }

    static <N> N createNullObjectClassFor(Class<N> aClass) {
        def nullObjectClass = new ByteBuddy().subclass(aClass)
                                             .name(aClass.getName() + "_NullObject")
                                             .method(CORE_LANG_METHODS_MATCHER).intercept(ExceptionMethod.throwing(UnsupportedOperationException,
                                                                                                                   "I am a NullObject and I cannot respond to this message."))
                                             .make()
                                             .load(Thread.currentThread().getContextClassLoader())
                                             .getLoaded()

        return nullObjectClass
    }

    static def newInstanceWithErrors(Class nullObjectClass, errors) {
        def metaClass = new ExpandoMetaClass(nullObjectClass, true, true)
        metaClass.initialize()
        def newInstance = nullObjectClass.newInstance()
        newInstance.setMetaClass(metaClass)
        newInstance.metaClass."onBuildSuccess" = {Closure aSuccessClosure -> return delegate}
        newInstance.metaClass."onBuildFailure" = {Closure aFailureClosure ->
            aFailureClosure(errors)
            return delegate
        }
        return newInstance
    }

    static List<String> coreLangMethods() {
        def methodsToRespond = ["setMetaClass", "getMetaClass", "getClass", "\$getStaticMetaClass"]
        methodsToRespond.addAll(Object.getDeclaredMethods().collect {it.name})
        methodsToRespond.addAll(GroovyObject.declaredMethods.name)
        return methodsToRespond
    }

    static void failIfCantCreateNullObject(Class aClass) {
        try {
            new ByteBuddy().subclass(aClass)
        } catch (IllegalArgumentException e) {
            if(e.message.contains("Cannot subclass primitive, array or final types")){
                throw new IllegalArgumentException("Cannot validate primitive or arrays, also cannot validate final types since I unable to subclass them and provide an automatic " +
                                                   "generated NullObject of it for you.")
            }

            throw e
        }
    }
}
