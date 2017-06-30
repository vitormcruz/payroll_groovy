package com.vmc.validationNotification.api

interface ValidationObserver {
    void validationStarted(String validationName)
    void mandatoryObligationIssued(Object subject, Map context, String mandatoryValidationName, String error)
    void mandatoryObligationComplied(Object subject, Map context, String mandatoryValidationName)
    void errorIssued(Object subject, Map context, String error)
    void validationFinished()
    Boolean successful()
}
