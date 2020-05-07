package com.vmc.userModel


import com.vmc.objectMemento.ObjectChangeProvider
import com.vmc.userModel.api.UserModel
import com.vmc.userModel.api.UserModelListener

import static com.vmc.userModel.ObjectUsageNotification.onObjectUnusedDo


class GeneralUserModel extends UserModel{

    protected WeakHashMap<UserModelListener, Void> observers = new WeakHashMap<UserModelListener, Void>()
    protected Set<ManagedObject> managedObjects = new HashSet()

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
        if(!managedObject.getObject().isDirty()){
            this.@managedObjects.remove(managedObject)
        }
    }

    Set getManagedObjects() {
       return new HashSet(this.@managedObjects)
    }

    @Override
    synchronized void save() {
        this.@managedObjects.each {it.save()}
        this.@observers.keySet().each {it.saveCalled(this)}
    }

    @Override
    synchronized void rollback() {
        this.@managedObjects.each {it.undo()}
        this.@observers.keySet().each {it.rollbackCalled(this)}
    }

    @Override
    void registerListener(UserModelListener listener) {
        observers.put(listener, void)
    }

    @Override
    void unregisterListener(UserModelListener listener) {
        observers.remove(listener)
    }

    Set<UserModelListener> getListeners() {
        observers.keySet()
    }
}
