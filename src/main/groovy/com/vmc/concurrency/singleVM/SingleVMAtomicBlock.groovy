package com.vmc.concurrency.singleVM

import com.vmc.concurrency.api.AtomicBlock

class SingleVMAtomicBlock implements AtomicBlock{

    @Override
    synchronized void execute(Closure unitOfWork) {
        unitOfWork()
    }

}
