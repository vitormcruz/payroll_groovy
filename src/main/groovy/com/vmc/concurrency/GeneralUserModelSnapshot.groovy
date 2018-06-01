package com.vmc.concurrency

import com.google.common.base.Preconditions
import com.vmc.DynamicClassFactory
import com.vmc.concurrency.api.SyncronizationBlock
import com.vmc.concurrency.api.UserModelSnapshot
import com.vmc.concurrency.api.UserSnapshotListener
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.modifier.Visibility
import net.bytebuddy.implementation.MethodDelegation

import static com.vmc.DynamicClassFactory.ALL_BUT_META_LANG_METHODS_MATCHER

class GeneralUserModelSnapshot<R extends Serializable> extends UserModelSnapshot<R>{
    
    public static final String TRACKING_PROXY_CLASS_SUFIX = "_TrackingProxy"

    protected WeakHashMap<UserSnapshotListener, Void> observers = new WeakHashMap<UserSnapshotListener, Void>()
    protected Set<ObjectTracker> userObjectsSnapshot = Collections.synchronizedSet(new HashSet<ObjectTracker>())
    protected SyncronizationBlock syncronizationBlock

    GeneralUserModelSnapshot() {
        this.syncronizationBlock = new SingleVMSyncronizationBlock()
    }

    GeneralUserModelSnapshot(SyncronizationBlock syncronizationBlock) {
        this.syncronizationBlock = syncronizationBlock
    }

    @Override
    R add(R object) {
        Preconditions.checkArgument(object instanceof Serializable, "I can only manage serializable objects")
        def objectSnapshot = object.takeSnapshot()
        Object trackingObjectProxy = createTrackingProxyFor(objectSnapshot)
        def objectTracker = new ObjectTracker(trackingObjectProxy, this.&removeUnusedObjectIfNotDirty)
        userObjectsSnapshot.add(objectTracker)
        if(trackingObjectProxy instanceof UserSnapshotListener) registerUnitOfWorkerListener(trackingObjectProxy)
        return trackingObjectProxy
    }

    Object createTrackingProxyFor(objectSnapshot) {
        def trackingProxyClass = DynamicClassFactory.getIfAbsentCreateAndManageWith(getCorrespondingTrackClassName(objectSnapshot),
                                                                                    { createTrackingProxyClassFor(objectSnapshot) })
        def objectProxy = trackingProxyClass.newInstance()
        objectProxy.subject = objectSnapshot
        return objectProxy
    }

    String getCorrespondingTrackClassName(entity) {
        "dynamic." + entity.getClass().getName() + TRACKING_PROXY_CLASS_SUFIX
    }

    def <S> Class<? extends S> createTrackingProxyClassFor(Class<S> aClass) {
        def nullObjectClass = new ByteBuddy().subclass(aClass)
                .name("dynamic." + aClass.getName() + TRACKING_PROXY_CLASS_SUFIX)
                .defineField("subject", aClass, Visibility.PUBLIC)
                .method(ALL_BUT_META_LANG_METHODS_MATCHER).intercept(MethodDelegation.toField("subject"))
                .make()
                .load(Thread.currentThread().getContextClassLoader())
                .getLoaded()
        return nullObjectClass
    }

    void removeUnusedObjectIfNotDirty(ObjectTracker unusedObjectTracker){
        if(!unusedObjectTracker.subjectIsDirty()){
            this.@userObjectsSnapshot.remove(unusedObjectTracker)
            unregisterUnitOfWorkerListener(unusedObjectTracker.subject)
        }
    }

    Set getUserObjectsSnapshot() {
       return new HashSet(this.@userObjectsSnapshot)
    }

    @Override
    void save() {
        syncronizationBlock.execute {
            this.@userObjectsSnapshot.each { ObjectTracker trackObject ->
                if(trackObject.subjectIsDirty()){
                    syncronizeObjectClosure(trackObject.subject)
                }
            }

            this.@observers.keySet().each {it.saveCalled(this)}
        }
    }

    @Override
    void rollback() {
        syncronizationBlock.execute {
            this.@observers.keySet().each {it.rollbackCalled(this)}
        }
    }

    @Override
    void registerUnitOfWorkerListener(UserSnapshotListener listener) {
        observers.put(listener, void)
    }

    @Override
    void unregisterUnitOfWorkerListener(UserSnapshotListener listener) {
        observers.remove(listener)
    }

    Set<UserSnapshotListener> getlisteners() {
        observers.keySet()
    }
}
