package com.vmc.concurrency.inMemory

import com.vmc.concurrency.AtomicBlock

class InMemoryAtomicBlock implements AtomicBlock{

    @Override
    synchronized void execute(Closure unitOfWork) {
        unitOfWork()
    }

}
