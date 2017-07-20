package com.vmc.concurrency.api

interface AtomicBlock {

    void execute(Closure c)

}