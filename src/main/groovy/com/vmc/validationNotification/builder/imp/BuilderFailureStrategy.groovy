package com.vmc.validationNotification.builder.imp

class BuilderFailureStrategy implements BuilderStrategy{

    @Override
    Boolean successful() {
        return false
    }

    @Override
    doWithBuiltEntity(builtObject, aSuccessClosure, aFailureClosure) {
        aFailureClosure()
        return null
    }
}
