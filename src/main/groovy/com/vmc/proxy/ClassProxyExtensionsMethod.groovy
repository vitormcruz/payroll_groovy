package com.vmc.proxy

import org.apache.commons.lang.ClassUtils


class ClassProxyExtensionsMethod {

    /**
     * Return all the methods from the inheritance chain that should be proxied by proxies. Ignores Object, GroovyObject methods as well as class methods such
     * as getMetaClass and those special methods added by groovy sufixed with '$'
     */
    static Collection<String> declaredMethodsFromMyInheritanceTree(Class aClass){
        List<Class> classes = ClassUtils.getAllSuperclasses(aClass)
        classes.remove(Object)
        classes.add(aClass)
        def methodsNames = classes.collectMany {it.getDeclaredMethods().name}
        methodsNames.removeAll({it.startsWith("\$")})
        methodsNames.removeAll(GroovyObject.methods.name)
        return methodsNames
    }
}
