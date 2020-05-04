package com.vmc.userModel.api


interface UserModelListener {

    void saveCalled(UserModel unitOfWork)
    void rollbackCalled(UserModel unitOfWork)
    void saveFailed(UserModel unitOfWork)
    void rollbackFailed(UserModel unitOfWork)
}