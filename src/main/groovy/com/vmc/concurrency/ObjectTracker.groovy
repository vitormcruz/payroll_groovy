package com.vmc.concurrency

import com.vmc.DynamicClassFactory
import org.apache.commons.lang3.StringUtils
import org.codehaus.groovy.classgen.asm.MopWriter

import java.util.regex.Matcher

/**
 * I track the subject proxy usage across the VM and notifies when the GC collected it. The idea is to substitute references to the subject for it's proxy, then when the proxy
 * gets collected by the VM, further finalization can be done to the subject. I also store the state of the original subject so that you can tell wheter there were changes on it ot
 * not.
 */
class ObjectTracker {

    public static final String TRACKING_PROXY_CLASS_SUFFIX = "_TrackingProxy"
    public static final List<String> META_LANG_METHODS = DynamicClassFactory.metaLangMethods()

    private static final GroovyClassLoader GROOVY_DYNAMIC_CLASS_LOADER = new GroovyClassLoader()

    protected trackedObject
    protected trackedObjectProxy

    ObjectTracker() {

    }

    ObjectTracker(trackedObject, Closure onGCRemoval) {
        initialize(trackedObject, onGCRemoval)
    }

    void initialize(trackedObject, Closure onGCRemoval) {
        this.trackedObject = trackedObject
        trackedObjectProxy = createTrackedProxyFor(trackedObject)
        ObjectUsageNotification.onObjectUnusedDo(trackedObjectProxy, {onGCRemoval(trackedObject)})
    }

    def createTrackedProxyFor(object) {
        def trackingProxyClass = DynamicClassFactory.getIfAbsentCreateAndManageWith(getCorrespondingTrackClassName(object),
                                                                                    { createTrackingProxyClassFor(object.class) })
        def objectProxy = trackingProxyClass.newInstance()
        objectProxy.@subject = object
        return objectProxy
    }

    String getCorrespondingTrackClassName(entity) {
        "dynamic." + entity.getClass().getName() + TRACKING_PROXY_CLASS_SUFFIX
    }


    static <T> Class<? extends T> createTrackingProxyClassFor(Class<T> aClass) {
        def trackingProxyClass = GROOVY_DYNAMIC_CLASS_LOADER.parseClass(/
            class ${aClass.getName().replaceAll("\\.", "_") + TRACKING_PROXY_CLASS_SUFFIX} 
            extends ${aClass.getName()} 
            implements GroovyInterceptable {

                public subject
            
                def invokeMethod(String methodName, Object args) {
                    if(com.vmc.concurrency.ObjectTracker.META_LANG_METHODS.contains(methodName)){
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
