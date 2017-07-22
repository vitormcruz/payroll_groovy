package com.vmc.validationNotification

import com.vmc.validationNotification.api.ValidationResult

//TODO Add tests, review, comment
class Validate extends SimpleValidationObserverImp {

    protected ValidationResult validationResult = new ValidationSuccess()
    protected executionResult

    static <R> R validate(Closure<R> aClusureToValidate) {
        return (R) new Validate(aClusureToValidate).getResult()
    }

    Validate(Closure aClosureToValidate) {
        ApplicationValidationNotifier.addObserver(this)
        executionResult = aClosureToValidate()
        ApplicationValidationNotifier.removeObserver(this)
        validationResult.setValidateObject(this)
    }

    ValidationResult getValidationResult() {
        return validationResult
    }

    def getResult(){
        return validationResult.getExecutionResult()
    }

    def getExecutionResult() {
        return executionResult
    }

    @Override
    void mandatoryObligationIssued(Object subject, Map context, String mandatoryValidationName, String error) {
        super.mandatoryObligationIssued(subject, context, mandatoryValidationName, error)
        validationResult = new ValidationFail()
    }

    @Override
    void mandatoryObligationComplied(Object subject, Map context, String mandatoryValidationName) {
        super.mandatoryObligationComplied(subject, context, mandatoryValidationName)
        if(super.mandatoryObligation.isEmpty()){ validationResult = new ValidationSuccess() }
    }

    @Override
    void errorIssued(Object subject, Map context, String error) {
        super.errorIssued(subject, context, error)
        validationResult = new ValidationFail()
    }

}
