package com.vmc.validationNotification

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
class Mandatory<T> {

    private T value
    private String errorMessage = "Mandatory value was not provided"
    private String mandatoryObligationId
    private Map context = [:]

    private setStrategy = { aNewValue ->
        if(aNewValue == null){
            mandatoryObligationId = issueMandatoryObligation(errorMessage, context)
            setStrategy = setStrategy_AfterMandatoryObligationIssued
        } else{
            value = aNewValue
            setStrategy = setStrategy_NormalAfterObligationComplied
        }
    }

    private setStrategy_AfterMandatoryObligationIssued = { aNewValue ->
        if(aNewValue != null){
            this.value = aNewValue
            issueMandatoryObligationComplied(mandatoryObligationId)
            setStrategy = setStrategy_NormalAfterObligationComplied
        }
    }

    private setStrategy_NormalAfterObligationComplied = { aNewValue ->
        aNewValue ? this.value = aNewValue : issueError(errorMessage, context)

    }

    /**
     * Creates a new mandatory holder with the value provided. If it's null, a mandatory obligation will be issued
     * right ahead. Uses a default error message.
     */
    Mandatory(T aValue) {
        setStrategy(aValue)
    }

    /**
     * Creates a new mandatory field holder with null value issuing a mandatory obligation right ahead. Uses a default
     * error message.
     */
    Mandatory() {
        this(null)
    }

    /**
     * Creates a new mandatory holder with the value, error message and context provided. If it's null, a mandatory obligation
     * will be issued right ahead.
     */
    Mandatory(T aValue, String errorMessage, Map context) {
        this.context = context
        this.errorMessage = errorMessage
        setStrategy(aValue)
    }

    /**
     * Return my value.
     * @throws IllegalStateException if my value is null
     */
    T get() {
        value ? value : {throw new IllegalStateException(errorMessage)}()
    }

    /**
     * Set a new value. An error will be issued and my value will not be changed f the new value is null.
     */
    def set(String aNewValue) {
        setStrategy(aNewValue)
        return this
    }
}
