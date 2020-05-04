package com.vmc.concurrency

import com.vmc.concurrency.api.ObjectChangeProvider
import com.vmc.concurrency.api.SynchronizationBlock
import com.vmc.concurrency.api.UserModelSnapshot
import com.vmc.concurrency.api.UserSnapshotListener

import static com.vmc.concurrency.ObjectUsageNotification.onObjectUnusedDo

class GeneralUserModelSnapshot extends UserModelSnapshot{

    protected WeakHashMap<UserSnapshotListener, Void> observers = new WeakHashMap<UserSnapshotListener, Void>()
    protected Set<ManagedObject> managedObjects = Collections.synchronizedSet(new HashSet())
    protected SynchronizationBlock synchronizationBlock

    //TODO remove instantiation from here and add to the main class
    protected ObjectProxyFactory objectProxyFactory = new ObjectProxyFactory()

    GeneralUserModelSnapshot() {
        this.synchronizationBlock = new SingleVMSynchronizationBlock()
    }

    GeneralUserModelSnapshot(SynchronizationBlock synchronizationBlock) {
        this.synchronizationBlock = synchronizationBlock
    }

    @Override
    <T> T manageObject(T object, ObjectChangeProvider objectChangeProvider) {
        object.takeSnapshot()
        def objectProxy = objectProxyFactory.createProxyFor(object)
        def managedObject = new ManagedObject(object, objectChangeProvider)
        managedObjects.add(managedObject)
        onObjectUnusedDo(objectProxy, {removeUnusedObjectIfNotDirty(managedObject)})
        return objectProxy
    }

    void removeUnusedObjectIfNotDirty(ManagedObject managedObject){
        if(!managedObject.getObject().isDirty()){
            this.@managedObjects.remove(managedObject)
        }
    }

    Set getManagedObjects() {
       return new HashSet(this.@managedObjects)
    }

    @Override
    void save() {
        synchronizationBlock.execute {
            this.@managedObjects.each {it.save()}
            this.@observers.keySet().each {it.saveCalled(this)}
        }
    }

    @Override
    void rollback() {
        synchronizationBlock.execute {
            this.@managedObjects.each {it.undo()}
            this.@observers.keySet().each {it.rollbackCalled(this)}
        }
    }

    @Override
    void registerListener(UserSnapshotListener listener) {
        observers.put(listener, void)
    }

    @Override
    void unregisterListener(UserSnapshotListener listener) {
        observers.remove(listener)
    }

    Set<UserSnapshotListener> getListeners() {
        observers.keySet()
    }
}
