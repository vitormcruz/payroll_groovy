package com.vmc.validationNotification


import static com.google.common.base.Preconditions.checkArgument
/**
 * I am a mandatory object holder. My value holder is only changed if it's the new value is not null. A mandatory
 * obligation will be issued if no value is provided upon creation and only when a non null value is set a mandatory
 * obligation complied will be issued.
 *
 * I am useful to provide an easy way of creating objects with temporary mandatory invalid state. Validation Observers
 * will consider that an error occurred when a mandatory obligation is issued without a par obligation complied, that
 * way objects may be created with null values for it's mandatory fields and then set to an appropriated value at a
 * later time. If the value is not set accordingly, validation observers will report error.
 */
class Mandatory {

    private String errorMessage
    private String mandatoryObligationId
    private Map context = [:]

    private setStrategy

    private setStrategy_AfterMandatoryObligationIssued = { aNewValue, setterFunction ->
        if(aNewValue != null){
            setterFunction(aNewValue)
            issueMandatoryObligationComplied(mandatoryObligationId)
            setStrategy = setStrategy_NormalAfterObligationComplied
        }
    }

    private setStrategy_NormalAfterObligationComplied = { aNewValue, setterFunction ->
        aNewValue ? setterFunction(aNewValue) : issueError(errorMessage, context)
    }

    /**
     * Creates a new mandatory holder with the value, error message and context provided. If it's null, a mandatory obligation
     * will be issued right ahead.
     */
    Mandatory(String errorMessage, Map context) {
        checkArgument(errorMessage != null, "Error message was not provided")

        mandatoryObligationId = issueMandatoryObligation(errorMessage, context)
        setStrategy = setStrategy_AfterMandatoryObligationIssued
        this.context = context
        this.errorMessage = errorMessage
    }

    def get(getterFunction){
        checkArgument(getterFunction != null, "Getter function was not provided")

        return getterFunction() ? getterFunction() : {throw new IllegalStateException(errorMessage)}()
    }

    void set(newValue, setterFunction){
        checkArgument(setterFunction  != null, "Setter function was not provided")
        setStrategy(newValue, setterFunction)

    }
}
