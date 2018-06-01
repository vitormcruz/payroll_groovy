package com.vmc.concurrency.api

/**
 *
 */
abstract class UserModelSnapshot<R extends Serializable> {

    private static currentUserModelSnapshot

    static UserModelSnapshot getInstance(){
        return currentUserModelSnapshot
    }

    static load(userModelSnapshot){
        currentUserModelSnapshot = userModelSnapshot
    }

    abstract R add(R object)

    /**
     * Save all the changes made in objects of the snapshot
     */
    abstract void save()

    /**
     * Rollback all changes mado into this snapshot. Objects without any further reference will be forgotten.
     */
    abstract void rollback()

    abstract void registerUnitOfWorkerListener(UserSnapshotListener listener)
    abstract void unregisterUnitOfWorkerListener(UserSnapshotListener listener)
}
