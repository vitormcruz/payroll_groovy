package com.vmc.concurrency.api

interface ModelSnapshot {

    /**
     * Save all the changes made in objects of the model
     */
    void save()

    void add(modelObject)
}
