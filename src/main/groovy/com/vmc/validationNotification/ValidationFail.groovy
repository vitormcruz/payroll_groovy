package com.vmc.validationNotification

import com.vmc.validationNotification.api.ValidationResult
//TODO Add tests
class ValidationFail implements ValidationResult{

    private DynamicNullValidatedObject nullObject

    @Override
    void setValidateObject(Validate validateObject) {
        this.nullObject = new DynamicNullValidatedObject(validateObject)
    }

    @Override
    getExecutionResult() {
        return nullObject.asValidatedObjectProxy()
    }
}
