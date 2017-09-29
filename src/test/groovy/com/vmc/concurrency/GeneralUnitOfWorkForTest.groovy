package com.vmc.concurrency

import com.sun.management.GarbageCollectionNotificationInfo

import javax.management.Notification
import javax.management.NotificationListener
import java.lang.management.ManagementFactory
import java.util.concurrent.TimeoutException

class GeneralUnitOfWorkForTest extends GeneralUnitOfWork implements NotificationListener{

    private HashSet<String> gcsWithNotificationHandled = Collections.synchronizedSet(new HashSet())

    @Override
    void notifyGcPerformed(GarbageCollectionNotificationInfo info) {
        super.notifyGcPerformed()
        this.@gcsWithNotificationHandled.add(info.gcName)
    }

    void waitWhileNotifyGcPerformedNotDoneFor(Integer timeout){
        int timeElapsed = 0
        def allGCsNotificationHandled
        def totalGCs = ManagementFactory.getGarbageCollectorMXBeans().size()

        synchronized (this.@phantonHandlerLock) {
            allGCsNotificationHandled = (this.@gcsWithNotificationHandled.size() == totalGCs)
        }

        while(!allGCsNotificationHandled && timeElapsed < timeout) {
            sleep(10)
            timeElapsed += 10
            synchronized (phantonHandlerLock) {
                allGCsNotificationHandled = (this.@gcsWithNotificationHandled.size() == totalGCs)
            }
        }

        if(!allGCsNotificationHandled) throw new TimeoutException("notifyGcPerformed was not called for all GCs. GCs size = ${totalGCs}; notifications = " +
                                                                  "${this.@gcsWithNotificationHandled}; Timeout = ${timeout}ms")
    }

    @Override
    void handleNotification(Notification notification, Object handback) {


    }
}
