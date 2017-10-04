package com.vmc.concurrency

import com.sun.management.GarbageCollectionNotificationInfo
import com.vmc.DynamicClassFactory
import com.vmc.payroll.external.config.ServiceLocator
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.modifier.Visibility
import net.bytebuddy.implementation.MethodDelegation
import org.apache.commons.lang3.SerializationUtils

import javax.management.Notification
import javax.management.NotificationEmitter
import javax.management.NotificationFilterSupport
import javax.management.NotificationListener
import javax.management.openmbean.CompositeData
import java.lang.management.ManagementFactory
import java.lang.ref.PhantomReference
import java.lang.ref.ReferenceQueue
import java.security.MessageDigest

import static com.sun.management.GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION
import static com.sun.management.GarbageCollectionNotificationInfo.from
import static com.vmc.DynamicClassFactory.ALL_BUT_CORE_LANG_METHODS_MATCHER

class GeneralUnitOfWork {

    public static final String TRACKING_PROXY_CLASS_SUFIX = "_TrackingProxy"

    protected static final phantonHandlerLock = new Object()
    protected static ReferenceQueue referenceQueue = new ReferenceQueue()
    protected static Map<PhantomReference, TrackedObject> trackedObjectsMap = Collections.synchronizedMap(new HashMap<PhantomReference, TrackedObject>())

    protected Map<PhantomReference, Map<String, Object>> userModel = Collections.synchronizedMap(new HashMap())

    static {
        NotificationFilterSupport notificationFilter = new NotificationFilterSupport()
        notificationFilter.enableType(GARBAGE_COLLECTION_NOTIFICATION)
        def handler = { Notification notification, Object handback -> notifyGcPerformed(notification) } as NotificationListener
        ManagementFactory.getGarbageCollectorMXBeans().each { NotificationEmitter emiter -> emiter.addNotificationListener(handler, notificationFilter , null)}
    }

    static void notifyGcPerformed(notification) {
        ServiceLocator.getInstance().getExecutor().execute({
            synchronized (phantonHandlerLock) {
                handleGcNotification(from((CompositeData) notification.getUserData()))
            }
        })
    }

    static void handleGcNotification(GarbageCollectionNotificationInfo info) {
        synchronized (phantonHandlerLock) {
            def phantonReference = referenceQueue.poll()
            while (phantonReference != null) {
                trackedObjectsMap.get(phantonReference).stopTracking()
                trackedObjectsMap.remove(phantonReference)
                phantonReference = referenceQueue.poll()
            }
        }
    }

    def <E> E addToUserModel(E entity) {
        if(!(entity instanceof Serializable)) return entity
        def trackingProxyClass = DynamicClassFactory.getDynamicCreatedClass(getCorrespondingTrackClassName(entity), { createTrackingProxyClassFor(entity.getClass()) })
        def trackingProxy = trackingProxyClass.newInstance()
        trackingProxy.proxySubject = entity
        userModel.put(entity, getEntityMD5(entity))
        trackedObjectsMap.put(new PhantomReference(trackingProxy, referenceQueue), new TrackedObject(entity, userModel))
        return trackingProxy
    }

    String getCorrespondingTrackClassName(entity) {
        "dynamic." + entity.getClass().getName() + TRACKING_PROXY_CLASS_SUFIX
    }

    public <C> Class<? extends C> createTrackingProxyClassFor(Class<C> aClass) {
        def nullObjectClass = new ByteBuddy().subclass(aClass)
                .name("dynamic." + aClass.getName() + TRACKING_PROXY_CLASS_SUFIX)
                .defineField("proxySubject", aClass, Visibility.PUBLIC)
                .method(ALL_BUT_CORE_LANG_METHODS_MATCHER).intercept(MethodDelegation.toField("proxySubject"))
                .make()
                .load(Thread.currentThread().getContextClassLoader())
                .getLoaded()

        return nullObjectClass
    }

    String getEntityMD5(entity) {
        return MessageDigest.getInstance("MD5").digest(SerializationUtils.serialize(entity)).encodeHex().toString()
    }

    Set getUserModel() {
        return new HashSet(this.@userModel.keySet())
    }

    static class TrackedObject{
        protected trackedObject
        protected Map<Object, String> trackMap

        TrackedObject(trackedObject, Map<Object, String> trackMap) {
            this.trackMap = trackMap
            this.trackedObject = trackedObject
        }

        void stopTracking(){
            trackMap.remove(trackedObject)
        }

    }


}
