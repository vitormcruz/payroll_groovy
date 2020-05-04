package com.vmc.concurrency

import com.vmc.concurrency.api.SynchronizationBlock

class SingleVMSynchronizationBlock implements SynchronizationBlock{

    @Override
    synchronized void execute(Closure unitOfWork) {
        unitOfWork()
    }

}
