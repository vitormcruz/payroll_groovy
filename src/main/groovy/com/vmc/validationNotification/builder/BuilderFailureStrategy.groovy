package com.vmc.validationNotification.builder

import com.vmc.validationNotification.builder.api.BuilderStrategy

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
