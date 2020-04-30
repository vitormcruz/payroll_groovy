package com.vmc.concurrency

import com.vmc.concurrency.api.SyncronizationBlock
import com.vmc.concurrency.api.UserModelSnapshot
import com.vmc.concurrency.api.UserSnapshotListener

class GeneralUserModelSnapshot extends UserModelSnapshot{
    
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
    def add(object) {
        object.takeSnapshot()
        def objectTracker = new ObjectTracker(object, this.&removeUnusedObjectIfNotDirty)
        userObjectsSnapshot.add(objectTracker.trackedObjectProxy)
        if(object instanceof UserSnapshotListener) registerUnitOfWorkerListener(object)
        return objectTracker.getTrackingProxy()
    }

    void removeUnusedObjectIfNotDirty(unusedObjectTracker){
        if(!unusedObjectTracker.isDirty()){
            this.@userObjectsSnapshot.remove(unusedObjectTracker)
            unregisterUnitOfWorkerListener(unusedObjectTracker)
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
            this.@userObjectsSnapshot.each {it.rollback()}
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
