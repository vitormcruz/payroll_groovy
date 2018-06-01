package com.vmc.validationNotification

import com.vmc.DynamicClassFactory
import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.ExceptionMethod

import static com.vmc.DynamicClassFactory.ALL_BUT_CORE_LANG_METHODS_MATCHER

//TODO review failIfCantCreateNullObject
class GenericNullObjectBuilder {

    static <N> N newNullObjectOf(Class<N> aClass, errors) {
        def nullObjectClass = DynamicClassFactory.getIfAbsentCreateAndManageWith(aClass.getName() + "_NullObject", {createNullObjectClassFor(aClass)})
        return newInstanceWithErrors(nullObjectClass, errors)
    }

    static <N> N createNullObjectClassFor(Class<N> aClass) {
        def nullObjectClass = new ByteBuddy().subclass(aClass)
                                             .name(aClass.getName() + "_NullObject")
                                             .method(ALL_BUT_CORE_LANG_METHODS_MATCHER).intercept(ExceptionMethod.throwing(UnsupportedOperationException,
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
