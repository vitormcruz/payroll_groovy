package com.vmc.validationNotification.builder.api

interface BuilderStrategy {
    Boolean successful()
    def doWithBuiltEntity(builtObject, aSuccessClosure, aFailureClosure)
}
