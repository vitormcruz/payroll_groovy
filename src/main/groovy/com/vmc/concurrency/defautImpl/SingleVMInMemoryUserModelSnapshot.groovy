package com.vmc.concurrency.defautImpl

import com.vmc.concurrency.GeneralUserModelSnapshot
import com.vmc.concurrency.api.SyncronizationBlock
import com.vmc.concurrency.api.UserSnapshotListener

class SingleVMInMemoryUserModelSnapshot extends GeneralUserModelSnapshot{

    private SyncronizationBlock atomicBlock
    private modelObjects = []

    SingleVMInMemoryUserModelSnapshot(SyncronizationBlock atomicBlock) {
        this.atomicBlock = atomicBlock
    }

    @Override
    synchronized void save() {
        atomicBlock.execute{
            modelObjects.each {it.executeAllPending()}
        }
    }

    @Override
    void rollback() {

    }

    @Override
    void registerUnitOfWorkerListener(UserSnapshotListener modelObject) {
        modelObjects.add(modelObject)
    }
}
