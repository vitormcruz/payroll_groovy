package com.vmc.validationNotification.builder.imp

class BuilderSuccessStrategy implements BuilderStrategy{

    @Override
    Boolean successful() {
        return true
    }

    @Override
    doWithBuiltEntity(Object builtObject, aSuccessClosure, aFailureClosure) {
        aSuccessClosure(builtObject)
        return builtObject
    }
}
