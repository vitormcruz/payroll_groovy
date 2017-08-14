package com.vmc.validationNotification

import com.google.common.collect.HashMultimap
import com.google.common.collect.Maps
import com.google.common.collect.Multimaps
import com.google.common.collect.SetMultimap
import com.vmc.validationNotification.api.ValidationObserver
import org.apache.commons.lang.ClassUtils
import org.apache.commons.lang.StringUtils

class SimpleValidationObserver implements ValidationObserver{

    protected Collection errors = []
    protected SetMultimap errorsByContext = new HashMultimap<Map.Entry<String, Object>, String>()
    protected Map mandatoryObligation = [:]

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
        errorsByContext.put(Maps.immutableEntry(issuerObjectContextName(), subject), error)
        addInheritanceChainAsClassContextForError(subject, error)
        context.each {this.@errorsByContext.put(Maps.immutableEntry(it.key, it.value), error)}
        errors.add(error)
    }

    public boolean addInheritanceChainAsClassContextForError(subject, String error) {
        def classes = ClassUtils.getAllSuperclasses(subject.getClass())
        classes.remove(Object)
        classes.add(subject.getClass())
        return classes.each {this.@errorsByContext.put(Maps.immutableEntry(issuerClassContextName(), it), error)}

    }

    //TODO think better way of doing this. Apply to classes properties as well
    public static String issuerObjectContextName(){
        return "issuerObject"
    }

    public static String issuerClassContextName(){
        return "issuerClass"
    }

    @Override
    void validationFinished() {

    }

    @Override
    Boolean successful() {
        return errors.isEmpty()
    }

    //TODO make tests for this
    @Override
    SetMultimap getErrorsByContext() {
        return Multimaps.unmodifiableSetMultimap(errorsByContext)
    }

    @Override
    def getErrors() {
        return errors + mandatoryObligation.collect {it.value}
    }

    String getCommaSeparatedErrors(){
        return StringUtils.join(getErrors(), ", ")
    }
}
