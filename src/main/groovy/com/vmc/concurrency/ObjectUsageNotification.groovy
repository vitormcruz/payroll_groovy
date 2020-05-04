package com.vmc.concurrency

import com.vmc.payroll.external.config.ServiceLocator

import javax.management.Notification
import javax.management.NotificationEmitter
import javax.management.NotificationFilterSupport
import javax.management.NotificationListener
import java.lang.management.ManagementFactory
import java.lang.ref.PhantomReference
import java.lang.ref.ReferenceQueue

import static com.sun.management.GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION

class ObjectUsageNotification {

    protected static final phantomHandlerLock = new Object()
    protected static ReferenceQueue referenceQueue = new ReferenceQueue()
    protected static Map<PhantomReference, Object> trackedObjectsMap = Collections.synchronizedMap(new HashMap<PhantomReference, ObjectUsageNotification>())

    static {
        NotificationFilterSupport notificationFilter = new NotificationFilterSupport()
        notificationFilter.enableType(GARBAGE_COLLECTION_NOTIFICATION)
        def handler = { Notification notification, Object handback -> notifyGcPerformed() } as NotificationListener
        ManagementFactory.getGarbageCollectorMXBeans().each { NotificationEmitter emiter -> emiter.addNotificationListener(handler, notificationFilter , null)}
    }

    static void notifyGcPerformed() {
        ServiceLocator.getInstance().getExecutor().execute({
            synchronized (phantomHandlerLock) {
                def phantonReference = referenceQueue.poll()
                while (phantonReference != null) {
                    trackedObjectsMap.get(phantonReference)()
                    trackedObjectsMap.remove(phantonReference)
                    phantonReference = referenceQueue.poll()
                }
            }
        })
    }

    static void onObjectUnusedDo(objectToTrack, notifyUnusedClosure) {
        trackedObjectsMap.put(new PhantomReference(objectToTrack, referenceQueue), notifyUnusedClosure)
    }


}
