package com.vmc.objectMemento

class InMemoryObjectChangeProvider implements ObjectChangeProvider{

    @Override
    void doOnCommit(Object anObject) {
        //Don't need to do anything
    }

    @Override
    void doOnRollback(Object anObject) {
        //Don't need to do anything
    }
}
