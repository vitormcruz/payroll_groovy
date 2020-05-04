package com.vmc.concurrency.api

//TODO transform into an interface? Create one?

/**
 *
 */
abstract class UserModel {

    private static currentUserModelSnapshot

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

    abstract void registerListener(UserSnapshotListener listener)
    abstract void unregisterListener(UserSnapshotListener listener)
}
