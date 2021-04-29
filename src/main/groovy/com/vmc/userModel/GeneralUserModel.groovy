package com.vmc.userModel


import com.vmc.objectMemento.ObjectChangeProvider
import com.vmc.userModel.api.UserModel
import com.vmc.userModel.api.UserModelListener

import static com.vmc.userModel.ObjectUsageNotification.onObjectUnusedDo

class GeneralUserModel extends UserModel {

    protected WeakHashMap<UserModelListener, Void> observers = new WeakHashMap<UserModelListener, Void>()
    protected Set<ManagedObject> managedObjects = new HashSet()
    private final Object actionLock = new Object()

    //TODO remove instantiation from here and add to the main class
    protected ObjectProxyFactory objectProxyFactory = new ObjectProxyFactory()

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
        synchronized (actionLock){
            if(!managedObject.getObject().isDirty()){
                this.@managedObjects.remove(managedObject)
            }
        }
    }

    @Override
    void save() {
        synchronized (actionLock) {
            this.@managedObjects.each { it.save() }
            this.@observers.keySet().each { it.saveCalled(this) }
        }
    }

    @Override
    void rollback() {
        synchronized (actionLock) {
            this.@managedObjects.each { it.undo() }
            this.@observers.keySet().each { it.rollbackCalled(this) }
        }
    }

    @Override
    void registerListener(UserModelListener listener) {
        observers.put(listener, void)
    }

    @Override
    void unregisterListener(UserModelListener listener) {
        observers.remove(listener)
    }

    Set getManagedObjects() {
        return this.@managedObjects.clone() as Set
    }

    Set<UserModelListener> getListeners() {
        return new HashSet<UserModelListener>(observers.keySet())
    }
}
