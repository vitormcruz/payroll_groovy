package com.vmc.concurrency

import com.vmc.DynamicClassFactory
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.modifier.Visibility
/**
 * I track the subject proxy usage across the VM and notifies when the GC collected it. The idea is to substitute references to the subject for it's proxy, then when the proxy
 * gets collected by the VM, further finalization can be done to the subject. I also store the state of the original subject so that you can tell wheter there were changes on it ot
 * not.
 */
class ObjectTracker {

    public static final String TRACKING_PROXY_CLASS_SUFIX = "_TrackingProxy"
    public static final List<String> META_LANG_METHODS = DynamicClassFactory.metaLangMethods()

    protected trackedObject
    protected trackedObjectProxy

    ObjectTracker() {}

    ObjectTracker(trackedObject, Closure onGCRemoval) {
        initialize(trackedObject, onGCRemoval)
    }

    void initialize(trackedObject, Closure onGCRemoval) {
        this.trackedObject = trackedObject
        trackedObjectProxy = createTrackedProxyFor(trackedObject)
        ObjectUsageNotification.onObjectUnusedDo(trackedObject, {onGCRemoval(trackedObject)})
    }

    def createTrackedProxyFor(objectSnapshot) {
        def trackingProxyClass = DynamicClassFactory.getIfAbsentCreateAndManageWith(getCorrespondingTrackClassName(objectSnapshot),
                                                                                    { createTrackingProxyClassFor(objectSnapshot.class) })
        def objectProxy = trackingProxyClass.newInstance()
        objectProxy.@subject = objectSnapshot
        return objectProxy
    }

    String getCorrespondingTrackClassName(entity) {
        "dynamic." + entity.getClass().getName() + TRACKING_PROXY_CLASS_SUFIX
    }

    static <S> Class<? extends S> createTrackingProxyClassFor(Class<S> aClass) {
        def trackingProxyClass = new ByteBuddy().subclass(aClass).implement(GroovyInterceptable)
                .name("dynamic." + aClass.getName() + TRACKING_PROXY_CLASS_SUFIX)
                .defineField("subject", aClass, Visibility.PUBLIC)
                .make()
                .load(Thread.currentThread().getContextClassLoader())
                .getLoaded()

        trackingProxyClass.metaClass.invokeMethod = {String methodName, Object args ->
            if(META_LANG_METHODS.contains(methodName)){
                return delegate.metaClass.getMetaMethod(methodName, args).invoke(delegate, args)
            }

            return delegate.@subject.invokeMethod(methodName, args)
        }

        trackingProxyClass.metaClass.getProperty = { String propertyName ->
            return delegate.@subject.getProperty(propertyName)
        }

        trackingProxyClass.metaClass.setProperty = { String propertyName, Object args ->
            return delegate.@subject.setProperty(propertyName, args)
        }

        return trackingProxyClass
    }

    def getTrackingProxy(){
        return trackedObjectProxy
    }
}
