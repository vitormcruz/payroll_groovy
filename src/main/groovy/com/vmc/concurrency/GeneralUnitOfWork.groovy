package com.vmc.concurrency

import com.sun.management.GarbageCollectionNotificationInfo
import com.vmc.DynamicClassFactory
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
    public static final String PROXY_SUBJECT = "subject"
    public static final String REAL_OBJECT_TAG = "realObject"
    public static final String MD5_TAG = "MD5"

    protected ReferenceQueue referenceQueue = new ReferenceQueue()
    protected NotificationListener handler = { Notification notification, Object handback ->
        Thread.start {
            synchronized (this.@phantonHandlerLock) {
                notifyGcPerformed(from((CompositeData) notification.getUserData()))
            }
        }
    } as NotificationListener

    protected Map<PhantomReference, Map<String, Object>> userModel = Collections.synchronizedMap(new HashMap())
    protected NotificationFilterSupport notificationFilter = new NotificationFilterSupport()
    protected final phantonHandlerLock = new Object()

    GeneralUnitOfWork() {
        notificationFilter.enableType(GARBAGE_COLLECTION_NOTIFICATION)
    }

    def <E> E addToUserModel(E entity) {
        if(!(entity instanceof Serializable)) return entity

        ManagementFactory.getGarbageCollectorMXBeans().each { NotificationEmitter emiter -> emiter.addNotificationListener(handler, notificationFilter , null)}
        def trackingProxyClass = DynamicClassFactory.getDynamicCreatedClass("dynamic." + entity.getClass().getName() + TRACKING_PROXY_CLASS_SUFIX, { createTrackingProxyClassFor(entity.getClass()) })
        def trackingProxy = trackingProxyClass.newInstance()
        trackingProxy."${PROXY_SUBJECT}" = entity
        this.@userModel.put(new PhantomReference(trackingProxy, referenceQueue), [(REAL_OBJECT_TAG): entity, (MD5_TAG): getEntityMD5(entity)])
        return trackingProxy
    }

    String getEntityMD5(entity) {
        return MessageDigest.getInstance(MD5_TAG).digest(SerializationUtils.serialize(entity)).encodeHex().toString()
    }

    static <C> Class<? extends C> createTrackingProxyClassFor(Class<C> aClass) {
        def nullObjectClass = new ByteBuddy().subclass(aClass)
                                             .name("dynamic." + aClass.getName() + TRACKING_PROXY_CLASS_SUFIX)
                                             .defineField(PROXY_SUBJECT, aClass, Visibility.PUBLIC)
                                             .method(ALL_BUT_CORE_LANG_METHODS_MATCHER).intercept(MethodDelegation.toField(PROXY_SUBJECT))
                                             .make()
                                             .load(Thread.currentThread().getContextClassLoader())
                                             .getLoaded()

        return nullObjectClass
    }

    void notifyGcPerformed(GarbageCollectionNotificationInfo info) {
        synchronized (phantonHandlerLock) {
            def phantonReference = referenceQueue.poll()
            while (phantonReference != null) {
                this.@userModel.remove(phantonReference)
                phantonReference = referenceQueue.poll()
            }
        }
    }

    Set getUserModel() {
        return new HashSet(this.@userModel.values().collect({it.entrySet()}).flatten().collect{entry ->
            if(entry.key == REAL_OBJECT_TAG) return entry.value
        })
    }


}
