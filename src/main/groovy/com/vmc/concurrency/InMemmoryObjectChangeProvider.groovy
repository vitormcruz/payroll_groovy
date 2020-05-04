package com.vmc.concurrency

import com.vmc.concurrency.api.ObjectChangeProvider

class InMemmoryObjectChangeProvider implements ObjectChangeProvider{

    @Override
    void doOnCommit(Object anObject) {
        //Don't need to do anything
    }

    @Override
    void doOnRollback(Object anObject) {
        //Don't need to do anything
    }
}
