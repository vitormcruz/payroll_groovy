package com.vmc.concurrency

import com.vmc.DynamicClassFactory
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.modifier.Visibility
import net.bytebuddy.implementation.MethodDelegation
import org.apache.commons.lang3.SerializationUtils

import java.security.MessageDigest

import static com.google.common.base.Preconditions.checkArgument
import static com.vmc.DynamicClassFactory.ALL_BUT_META_LANG_METHODS_MATCHER

class TrackObject<T> {

    private static UUID validConstructorCheck = UUID.randomUUID()

    protected Serializable trackedObject
    protected String trackedObjectMD5
    protected Closure unusedUnchangedNotification
    protected Class trackingProxyClass
    public static final String TRACKING_PROXY_CLASS_SUFIX = "_TrackingProxy"

    static <E> TrackObject<E> newTrackedObject(E objectToTrack, Closure unusedNotification){
        TrackObject trackObject = new TrackObject(objectToTrack, unusedNotification, validConstructorCheck)
        return trackObject
    }

    TrackObject() {}

    TrackObject(Serializable trackedObject, Closure unusedNotification, UUID constructorCheck) {
        initialize(unusedNotification, trackedObject, constructorCheck)
    }

    void initialize(Closure unusedNotification, Serializable trackedObject, UUID constructorCheck) {
        checkArgument(validConstructorCheck == constructorCheck, "I must be created using one of my factory methods. You should not use my constructor directly.")
        this.unusedUnchangedNotification = unusedNotification
        this.trackedObject = trackedObject
        trackedObjectMD5 = getObjectMD5(trackedObject)
        trackingProxyClass = DynamicClassFactory.getDynamicCreatedClass(getCorrespondingTrackClassName(trackedObject), { createTrackingProxyClassFor(trackedObject.getClass()) })
    }

    String getObjectMD5(entity) {
        return MessageDigest.getInstance("MD5").digest(SerializationUtils.serialize(entity)).encodeHex().toString()
    }

    String getCorrespondingTrackClassName(entity) {
        "dynamic." + entity.getClass().getName() + TRACKING_PROXY_CLASS_SUFIX
    }

    public <C> Class<? extends C> createTrackingProxyClassFor(Class<C> aClass) {
        def nullObjectClass = new ByteBuddy().subclass(aClass)
                                             .name("dynamic." + aClass.getName() + TRACKING_PROXY_CLASS_SUFIX)
                                             .defineField("proxySubject", aClass, Visibility.PUBLIC)
                                             .method(ALL_BUT_META_LANG_METHODS_MATCHER).intercept(MethodDelegation.toField("proxySubject"))
                                             .make()
                                             .load(Thread.currentThread().getContextClassLoader())
                                             .getLoaded()
        return nullObjectClass
    }

    Serializable getTrackedObject() {
        return trackedObject
    }

    T newTrackingProxyForMe(){
        def trackingProxy = trackingProxyClass.newInstance()
        trackingProxy.proxySubject = trackedObject
        ObjectUsageNotification.registerUnusedListener(trackingProxy, {this.intanceBecomeUnused()})
        return trackingProxy
    }

    void intanceBecomeUnused(){
        unusedUnchangedNotification(this)
    }

    boolean isNotDirty() {
        return !isDirty()
    }

    boolean isDirty() {
        return getObjectMD5(trackedObject) != trackedObjectMD5
    }
}
