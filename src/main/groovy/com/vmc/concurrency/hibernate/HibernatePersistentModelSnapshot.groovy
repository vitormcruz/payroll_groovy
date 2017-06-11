package com.vmc.concurrency.hibernate

import com.vmc.concurrency.AtomicBlock
import com.vmc.concurrency.ModelSnapshot

class HibernatePersistentModelSnapshot implements ModelSnapshot{

    private AtomicBlock atomicBlock = AtomicBlock.smartNewFor(HibernatePersistentModelSnapshot)
    private modelObjects = []

    @Override
    def void save() {
        atomicBlock.execute{
            modelObjects.each { it.executeAllPending()}
        }
    }

    @Override
    void add(Object modelObject) {
        modelObjects.add(modelObject)
    }
}
