package com.vmc.concurrency

import com.vmc.concurrency.api.SyncronizationBlock

class SingleVMSyncronizationBlock implements SyncronizationBlock{

    @Override
    synchronized void execute(Closure unitOfWork) {
        unitOfWork()
    }

}
