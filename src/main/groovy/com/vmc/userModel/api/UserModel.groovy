package com.vmc.userModel.api

import com.vmc.objectMemento.ObjectChangeProvider

//TODO transform into an interface? Create one?

/**
 *
 */
abstract class UserModel {

    private static currentUserModelSnapshot

    //TODO remove those get instances and put instatiation on main
    static UserModel getInstance(){
        return currentUserModelSnapshot
    }

    static load(userModelSnapshot){
        currentUserModelSnapshot = userModelSnapshot
    }

    abstract <T> T manageObject(T anObject, ObjectChangeProvider objectChangeProvider)

    /**
     * Save all the changes made in objects of the snapshot
     */
    abstract void save()

    /**
     * Rollback all changes managed by this model.
     */
    abstract void rollback()

    abstract void registerListener(UserModelListener listener)
    abstract void unregisterListener(UserModelListener listener)
}
