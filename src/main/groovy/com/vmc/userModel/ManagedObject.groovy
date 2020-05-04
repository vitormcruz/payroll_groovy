package com.vmc.userModel


import com.vmc.objectMemento.ObjectChangeProvider

class ManagedObject {
    private Object object
    private ObjectChangeProvider objectChangeProvider

    ManagedObject(object, ObjectChangeProvider objectChangeProvider) {
        this.objectChangeProvider = objectChangeProvider
        this.object = object
    }

    void save() {
        if(object.isDirty()){
            //TODO add commit when implemented
           objectChangeProvider.doOnCommit(object)
        }
    }

    void undo() {
        if(object.isDirty()){
            object.rollback()
            objectChangeProvider.doOnRollback(object)
        }
    }

    Object getObject() {
        return object
    }
}
