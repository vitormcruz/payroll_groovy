package com.vmc.concurrency.inMemory

import com.vmc.concurrency.AtomicBlock
import com.vmc.concurrency.ModelSnapshot

class InMemoryPersistentModelSnapshot implements ModelSnapshot{

    private AtomicBlock atomicBlock
    private modelObjects = []

    InMemoryPersistentModelSnapshot(AtomicBlock atomicBlock) {
        this.atomicBlock = atomicBlock
    }

    @Override
    synchronized void save() {
        atomicBlock.execute{
            modelObjects.each {it.executeAllPending()}
        }
    }

    @Override
    void add(Object modelObject) {
        modelObjects.add(modelObject)
    }
}
