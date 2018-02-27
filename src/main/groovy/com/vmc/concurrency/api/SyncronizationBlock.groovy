package com.vmc.concurrency.api

interface SyncronizationBlock {

    void execute(Closure c)

}