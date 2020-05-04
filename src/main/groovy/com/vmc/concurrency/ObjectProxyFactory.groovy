package com.vmc.concurrency

import com.vmc.DynamicClassFactory
import org.apache.commons.lang3.StringUtils
import org.codehaus.groovy.classgen.asm.MopWriter

import java.util.regex.Matcher

/**
 * I am a factory of proxies. I can create a proxy of any object and it's class will be a subclass of the real (subject)
 * object. For example, creating a proxy for a java.util.Date object d1 will return a d1_proxy of the dynamically
 * generated class java.util.dynamic.Date_Proxy such that:
 *
 * <pre>
 *     class java.util.dynamic.Date_Proxy <b>extends</b> java.util.Date
 * </pre>
 *
 * Almost all methods are proxied, even those from Object, only meta methods aren't.
 */
class ObjectProxyFactory {

    public static final String PROXY_CLASS_PREFIX = "dynamic."
    public static final String PROXY_CLASS_SUFFIX = "_Proxy"

    public static final List<String> META_LANG_METHODS = ["setMetaClass", "getMetaClass", "getClass", "\$getStaticMetaClass"]
    private static final GroovyClassLoader GROOVY_DYNAMIC_CLASS_LOADER = new GroovyClassLoader()

    def createProxyFor(object) {
        def proxyClass =
                DynamicClassFactory.getIfAbsentCreateAndManageWith(getProxyClassNameFor(object.getClass()),
                                                                  { createProxyClassFor(object.getClass()) })
        def objectProxy = proxyClass.newInstance()
        objectProxy.@subject = object
        return objectProxy
    }

    String getProxyClassNameFor(Class<? extends Object> aClass) {
        PROXY_CLASS_PREFIX + aClass.getName() + PROXY_CLASS_SUFFIX
    }

    static <T> Class<? extends T> createProxyClassFor(Class<T> aClass) {
        def trackingProxyClass = GROOVY_DYNAMIC_CLASS_LOADER.parseClass(/
            class ${aClass.getName().replaceAll("\\.", "_") + PROXY_CLASS_SUFFIX} 
            extends ${aClass.getName()} 
            implements GroovyInterceptable {

                public subject
            
                def invokeMethod(String methodName, Object args) {
                    if(com.vmc.concurrency.ObjectProxyFactory.META_LANG_METHODS.contains(methodName)){
                        return this.getMetaMethod(methodName, args).invoke(this, args)
                    }
            
                    return this.@subject.invokeMethod(methodName, args)
                }
            
                def getProperty(String propertyName) {
                    return this.@subject.getProperty(propertyName)
                }
            
                void setProperty(String propertyName, Object newValue){
                    this.@subject.setProperty(propertyName, newValue)
                }

                ${delegateMethodsOf(aClass)}
            }
        /)
        return trackingProxyClass
    }

    private static <T> String delegateMethodsOf(Class<T> aClass) {
        def delegatedMethodsDeclarations =
                (aClass.methods - Object.methods)
                    .findAll { !specialMethodNames(it.name) }
                    .unique {a, b -> a.name == b.name ? 0 : 1 }
                    .collect {
                        def methodNameToDelegate = it.name.replaceAll(Matcher.quoteReplacement("\$"),
                                                                      Matcher.quoteReplacement("\\\$"))
                        def returnType = it.getReturnType().getName()
                        def parameters = it.typeParameters.length == 0 ? "" : "args"
                        def delegation = "this.@subject.\"${methodNameToDelegate}\"()"

                        "${returnType} \"${methodNameToDelegate}\"(${parameters}) { ${delegation} } \n "
                    }

        return StringUtils.join(delegatedMethodsDeclarations, '\n')
    }

    private static boolean specialMethodNames(String methodName) {
        META_LANG_METHODS.contains(methodName) || MopWriter.isMopMethod(methodName)
    }
}
