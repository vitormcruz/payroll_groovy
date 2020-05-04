package com.vmc.concurrency.api

//TODO reorganize packages, concurrency doen't make sense
interface ObjectChangeProvider {

    /**
     * Will be called when the model is saved.
     */
    void doOnCommit(anObject)

    /**
     * Will be called when the model is rolled back.
     */
    void doOnRollback(anObject)

}