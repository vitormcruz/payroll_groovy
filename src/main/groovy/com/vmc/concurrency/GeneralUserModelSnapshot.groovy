package com.vmc.concurrency

import com.vmc.concurrency.api.SyncronizationBlock
import com.vmc.concurrency.api.UserModelSnapshot
import com.vmc.concurrency.api.UserSnapshotListener
import com.vmc.concurrency.defautImpl.SingleVMSyncronizationBlock

class GeneralUserModelSnapshot<R extends Serializable> extends UserModelSnapshot{

    protected WeakHashMap<UserSnapshotListener, Void> observers = new WeakHashMap<UserSnapshotListener, Void>()
    protected Map<TrackObject, Closure> userObjectsSnapshot = Collections.synchronizedMap(new HashMap<TrackObject, Closure>())
    protected SyncronizationBlock syncronizationBlock

    GeneralUserModelSnapshot() {
        this.syncronizationBlock = new SingleVMSyncronizationBlock()
    }

    GeneralUserModelSnapshot(SyncronizationBlock syncronizationBlock) {
        this.syncronizationBlock = syncronizationBlock
    }

    R addToUserSnapshot(R entity, syncronizeObjectClosure) {
        if(!(entity instanceof Serializable)) return entity
        def trackedObject = TrackObject.newTrackedObject(entity, { removeUnusedObjectIfNotDirty(it) })
        userObjectsSnapshot.put(trackedObject, syncronizeObjectClosure)
        return trackedObject.newTrackingProxyForMe()
    }

    void removeUnusedObjectIfNotDirty(TrackObject unusedObject){
        if(unusedObject.isNotDirty()){
            this.@userObjectsSnapshot.remove(unusedObject)
        }
    }

    Set getUserObjectsSnapshot() {
       return new HashSet(this.@userObjectsSnapshot.keySet().collect({it.getTrackedObject()}))
    }

    void save() {
        syncronizationBlock.execute {
            this.@userObjectsSnapshot.each { trackObject, syncronizeObjectClosure ->
                if(trackObject.isDirty()){
                    syncronizeObjectClosure(trackObject.trackedObject)
                }
            }

            this.@observers.keySet().each {it.saveCalled(this)}
        }
    }

    @Override
    void rollback() {

    }

    Serializable register(Serializable object) {
        return null
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
