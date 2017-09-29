package com.vmc

import net.bytebuddy.matcher.ElementMatcher

import static net.bytebuddy.matcher.ElementMatchers.named
import static net.bytebuddy.matcher.ElementMatchers.not

class DynamicClassFactory {

    public static final ElementMatcher ALL_BUT_CORE_LANG_METHODS_MATCHER = coreLangMethods().collect { methodName -> not(named(methodName)) }
                                                                                            .inject { acc, val -> acc.and(val) }

    private static nullObjectByName = [:]

    static List<String> coreLangMethods() {
        def methodsToRespond = ["setMetaClass", "getMetaClass", "getClass", "\$getStaticMetaClass"]
        methodsToRespond.addAll(Object.getDeclaredMethods().collect {it.name})
        methodsToRespond.addAll(GroovyObject.declaredMethods.name)
        return methodsToRespond
    }

    static Class getDynamicCreatedClass(String className, Closure<Class> dynamicClassCreationMethod) {
        Class<?> dynamicCreatedClass = nullObjectByName.get(className)
        if (dynamicCreatedClass == null) {
            dynamicCreatedClass = dynamicClassCreationMethod(className)
            nullObjectByName.put(className, dynamicCreatedClass)
        }

        return dynamicCreatedClass
    }
}
