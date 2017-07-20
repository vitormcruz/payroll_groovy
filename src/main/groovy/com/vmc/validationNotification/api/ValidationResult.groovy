package com.vmc.validationNotification.api

import com.vmc.validationNotification.Validate

interface ValidationResult {

    void setValidateObject(Validate validateObject)

    def getExecutionResult()
}
