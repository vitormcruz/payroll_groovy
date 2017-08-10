package com.vmc.validationNotification

import com.vmc.validationNotification.api.ValidationResult
import com.vmc.validationNotification.builder.ConstructionValidationFailedException

//TODO Add tests, review, comment
class Validate extends SimpleValidationObserverImp {

    protected ValidationResult validationResult = new ValidationSuccess()
    protected executionResult
    private Class classValidated

    static <R> R validate(Class<R> classValidated, Closure<R> aClusureToValidate) {
        return (R) new Validate(classValidated, aClusureToValidate).getResult()
    }

    Validate(Class classValidated, Closure aClosureToValidate) {
        this.classValidated = classValidated
        ApplicationValidationNotifier.addObserver(this)
        executeIgnoringConstructorValidationException(aClosureToValidate)
        ApplicationValidationNotifier.removeObserver(this)
        validationResult.setValidateObject(this)
    }

    private void executeIgnoringConstructorValidationException(Closure aClosureToValidate) {
        try {
            executionResult = aClosureToValidate()
        } catch (ConstructionValidationFailedException e) {
            //Ignored. Validation failure will be treated differently.
        }
    }

    ValidationResult getValidationResult() {
        return validationResult
    }

    Class getClassValidated() {
        return classValidated
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
