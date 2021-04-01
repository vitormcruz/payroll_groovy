package com.vmc.payroll.adapter.web.spark.servlet


import com.google.common.collect.SetMultimap
import com.vmc.validationNotification.SimpleValidationObserver
import com.vmc.validationNotification.api.ValidationObserver
import org.apache.http.HttpStatus
import spark.Response

class ValidationAwareResponse extends Response implements ValidationObserver{

    @Delegate
    private Response responseSubject

    @Delegate
    private SimpleValidationObserver validationObserver = new SimpleValidationObserver()

    def private fillResponseStrategy
    def private issueErrorStrategy
    def body

    ValidationAwareResponse(Response responseSubject) {
        this.responseSubject = responseSubject
        fillResponseStrategy = responseOkStrategy
        issueErrorStrategy = issueFirstErrorStrategy
    }

    Map<String, Collection> getErrorsByValidation(){
        return errorsByValidation.findAll {!it.value.isEmpty()}
    }

    @Override
    void errorIssued(Object subject, Map context, String error) {
        issueErrorStrategy(subject, context, error)
    }

    def private issueFirstErrorStrategy = { subject, context, error ->
        issueErrorOnly(subject, context, error)
        fillResponseStrategy = responseFailStrategy
        issueFirstErrorStrategy = issueErrorOnly
    }

    def private issueErrorOnly = { subject, context, error ->
        validationObserver.errorIssued(subject, context, error)
    }

    @Override
    def getErrors() {
        return null
    }

    @Override
    SetMultimap getErrorsByContext() {
        return null
    }

    def fillResponse(){
        fillResponseStrategy(responseSubject)
    }

    def private responseOkStrategy = {Response res ->
        res.status(HttpStatus.SC_OK)
        res.body(body.toJson())
    }

    def private responseFailStrategy = {Response res ->
        res.status(HttpStatus.SC_BAD_REQUEST)
        res.body(validationObserver.errors.toJson())
    }
}
