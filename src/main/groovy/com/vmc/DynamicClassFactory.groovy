package com.vmc

import net.bytebuddy.matcher.ElementMatcher

import static net.bytebuddy.matcher.ElementMatchers.named
import static net.bytebuddy.matcher.ElementMatchers.not

class DynamicClassFactory {

    public static final ElementMatcher ALL_BUT_CORE_LANG_METHODS_MATCHER = coreLangMethods().collect { methodName -> not(named(methodName)) }
                                                                                            .inject { acc, val -> acc.and(val) }

    public static final ElementMatcher ALL_BUT_META_LANG_METHODS_MATCHER = metaLangMethods().collect { methodName -> not(named(methodName)) }
                                                                                            .inject { acc, val -> acc.and(val) }

    private static final Map<String, Class> dynamicClassByName = [:]

    static List<String> coreLangMethods() {
        def methodsToRespond = metaLangMethods()
        methodsToRespond.addAll(Object.getDeclaredMethods().collect {it.name})
        methodsToRespond.addAll(GroovyObject.declaredMethods.name)
        return methodsToRespond
    }

    static List<String> metaLangMethods() {
        def methodsToRespond = ["setMetaClass", "getMetaClass", "getClass", "\$getStaticMetaClass"]
        return methodsToRespond
    }

    static Class getIfAbsentCreateAndManageWith(String className, Closure<Class> createClassIfAbsent) {
        Class<?> dynamicCreatedClass = dynamicClassByName.get(className)
        if (dynamicCreatedClass == null) {
            dynamicCreatedClass = createClassIfAbsent(className)
            dynamicClassByName.put(className, dynamicCreatedClass)
        }

        return dynamicCreatedClass
    }
}
