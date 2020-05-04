package com.vmc.concurrency.api


interface UserSnapshotListener {

    void saveCalled(UserModel unitOfWork)
    void rollbackCalled(UserModel unitOfWork)
    void saveFailed(UserModel unitOfWork)
    void rollbackFailed(UserModel unitOfWork)
}