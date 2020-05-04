package com.vmc.concurrency.api

interface SynchronizationBlock {

    void execute(Closure c)

}