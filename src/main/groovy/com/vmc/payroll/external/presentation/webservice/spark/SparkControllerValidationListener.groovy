package com.vmc.payroll.external.presentation.webservice.spark

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.SetMultimap
import com.vmc.validationNotification.SimpleValidationObserver
import com.vmc.validationNotification.api.ValidationObserver
import org.apache.http.HttpStatus
import spark.Response

class SparkControllerValidationListener extends SimpleValidationObserver implements ValidationObserver{

    def private fillResponseStrategy = responseOkStrategy
    def private issueErrorStrategy = issueFirstErrorStrategy
    def body

    private ObjectMapper mapper = new ObjectMapper()

    SparkControllerValidationListener() {
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
        super.issueError(subject, context, error)
    }

    @Override
    def getErrors() {
        return null
    }

    @Override
    SetMultimap getErrorsByContext() {
        return null
    }

    def fillResponse(Response response){
        fillResponseStrategy(response)
    }

    def private responseOkStrategy = {Response res ->
        res.status(HttpStatus.SC_OK)
        res.body(mapper.writeValueAsString(body))
    }

    def private responseFailStrategy = {Response res ->
        res.status(HttpStatus.SC_BAD_REQUEST)
        res.body(mapper.writeValueAsString(super.errorsByContext))
    }
}
