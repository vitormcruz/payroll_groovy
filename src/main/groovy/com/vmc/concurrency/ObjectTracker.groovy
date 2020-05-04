package com.vmc.concurrency

import com.vmc.DynamicClassFactory
import org.apache.commons.lang3.StringUtils
import org.codehaus.groovy.classgen.asm.MopWriter

import java.lang.reflect.Method
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
        objectProxy.@"${ProxyTrait.getName().replaceAll("\\.", "_")}__subject" = object
        return objectProxy
    }

    String getCorrespondingTrackClassName(entity) {
        "dynamic." + entity.getClass().getName() + TRACKING_PROXY_CLASS_SUFFIX
    }


    static <S> Class<? extends S> createTrackingProxyClassFor(Class<S> aClass) {
        def trackingProxyClass = GROOVY_DYNAMIC_CLASS_LOADER.parseClass(/
            class ${aClass.getName().replaceAll("\\.", "_") + TRACKING_PROXY_CLASS_SUFFIX} 
            extends ${aClass.getName()} 
            implements ${ProxyTrait.getName()} {

                ${ddd(aClass)}
            }
        /)

//        trackingProxyClass.metaClass.invokeMethod = {String methodName, Object args ->
//            if(META_LANG_METHODS.contains(methodName)){
//                return delegate.metaClass.getMetaMethod(methodName, args).invoke(delegate, args)
//            }
//
//            return delegate.@subject.invokeMethod(methodName, args)
//        }
//
//        trackingProxyClass.metaClass.getProperty = { String propertyName ->
//            return delegate.@subject.getProperty(propertyName)
//        }
//
//        trackingProxyClass.metaClass.setProperty = { String propertyName, Object args ->
//            return delegate.@subject.setProperty(propertyName, args)
//        }

        return trackingProxyClass
    }

    private static String ddd(clazz) {
        Method[] methods = clazz.methods - Object.methods
        def r = [ ] as Set
        methods = methods.collect {
            if(META_LANG_METHODS.contains(it.name) || MopWriter.isMopMethod(it.name)){
                return null
            }

            if (!r.contains(it.name)){
                r.add(it.name)
                return it
            }

            return null

        }.findAll {it != null}

        return StringUtils.join(methods.collect {
            def methodNameToDelegate = it.name.replaceAll(Matcher.quoteReplacement("\$"),
                                                          Matcher.quoteReplacement("\\\$"))
            return "${it.getReturnType().getName()} \"${methodNameToDelegate}\"(${it.typeParameters.length == 0 ? "" : "args"}) { this.@\"com_vmc_concurrency_ProxyTrait__subject\".\"${methodNameToDelegate}\"()} \n "
        }, '\n')
    }

    def getTrackingProxy(){
        return trackedObjectProxy
    }
}
