package com.vmc.concurrency

import org.apache.commons.lang3.SerializationUtils

import java.security.MessageDigest
/**
 * I track the subject proxy usage across the VM and notifies when the GC collected it. The idea is to substitute references to the subject for it's proxy, then when a it's proxy
 * gets collected by the VM further finalization can be done to the subject. I also store the state of the original subject so that you can tell wheter there were changes on it ot
 * not.
 *
 * @param <S> The proxy subject.
 */
class ObjectTracker<S extends Serializable> {

    protected S trackedObject
    protected byte[] trackedObjectMD5

    ObjectTracker() {}

    ObjectTracker(S trackedObject, Closure onGCRemoval) {
        initialize(trackedObject, onGCRemoval)
    }

    void initialize(S trackedObject, Closure onGCRemoval) {
        this.trackedObject = trackedObject
        trackedObjectMD5 = getObjectMD5(trackedObject)
        ObjectUsageNotification.onObjectUnusedDo(trackedObject, {onGCRemoval(trackedObject)})
    }

    boolean subjectIsDirty() {
        return getObjectMD5(trackedObject) != trackedObjectMD5
    }

    byte[] getObjectMD5(entity) {
        return MessageDigest.getInstance("MD5").digest(SerializationUtils.serialize(entity))
    }

}
