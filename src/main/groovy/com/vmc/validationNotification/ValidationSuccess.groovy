package com.vmc.validationNotification

import com.vmc.validationNotification.api.ValidationResult

class ValidationSuccess implements ValidationResult{

    private Validate validateObject

    @Override
    void setValidateObject(Validate validateObject) {
        this.validateObject = validateObject
    }

    @Override
    getExecutionResult() {
        return validateObject.executionResult
    }
}
