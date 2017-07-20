package com.vmc.concurrency.singleVM

import com.vmc.concurrency.api.AtomicBlock
import com.vmc.concurrency.api.ModelSnapshot

class SingleVMModelSnapshot implements ModelSnapshot{

    private AtomicBlock atomicBlock
    private modelObjects = []

    SingleVMModelSnapshot(AtomicBlock atomicBlock) {
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
