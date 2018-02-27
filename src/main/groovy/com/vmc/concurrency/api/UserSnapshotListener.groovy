package com.vmc.concurrency.api


interface UserSnapshotListener {

    void saveCalled(UserModelSnapshot unitOfWork)
    void rollbackCalled(UserModelSnapshot unitOfWork)
    void saveFailed(UserModelSnapshot unitOfWork)
    void rollbackFailed(UserModelSnapshot unitOfWork)
}