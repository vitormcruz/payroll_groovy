package com.vmc.validationNotification.builder

import com.vmc.validationNotification.builder.api.BuilderStrategy

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
