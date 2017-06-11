package com.vmc.concurrency.inMemory

import com.vmc.concurrency.AtomicBlock

class InMemoryAtomicBlock implements AtomicBlock{

    @Override
    public synchronized void execute(Closure unitOfWork) {
        unitOfWork()
    }

}
