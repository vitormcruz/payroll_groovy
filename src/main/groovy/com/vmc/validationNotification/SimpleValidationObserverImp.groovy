package com.vmc.validationNotification

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import com.google.common.collect.Tables
import com.vmc.validationNotification.api.SimpleValidationObserver
import org.apache.commons.lang.StringUtils

class SimpleValidationObserverImp implements SimpleValidationObserver{

    private Collection errors = []
    private Table<Object, Object, String> errorsByContext = HashBasedTable.create()
    private Map mandatoryObligation = [:]

    @Override
    void validationStarted(String validationName) {

    }

    @Override
    void mandatoryObligationIssued(Object subject, Map context, String mandatoryValidationName, String error) {
        mandatoryObligation.put(mandatoryValidationName, error)
    }

    @Override
    void mandatoryObligationComplied(Object subject, Map context, String mandatoryValidationName) {
        mandatoryObligation.remove(mandatoryValidationName)
    }

    @Override
    void errorIssued(Object subject, Map context, String error) {
        if(context.isEmpty()){
            errorsByContext.put("", "", error)
        }else {
            context.each {this.@errorsByContext.put(it.key, it.value, error)}
        }

        errors.add(error)
    }

    @Override
    void validationFinished() {

    }

    @Override
    Boolean successful() {
        return errors.isEmpty()
    }

    @Override
    Table<Object, Object, String> getErrorsByContext() {
        return Tables.unmodifiableTable(errorsByContext)
    }

    @Override
    def getErrors() {
        return errors + mandatoryObligation.collect {it.value}
    }

    def getCommaSeparatedErrors(){
        return StringUtils.join(getErrors(), ", ")
    }
}
