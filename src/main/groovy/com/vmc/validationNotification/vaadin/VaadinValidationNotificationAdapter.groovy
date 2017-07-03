package com.vmc.validationNotification.vaadin

import com.vaadin.ui.AbstractComponent
import com.vmc.validationNotification.ApplicationValidationNotifier
import com.vmc.validationNotification.SimpleValidationObserverImp

class VaadinValidationNotificationAdapter {

    private validationObserver
    private validations = []

    def bindValidationFor(String propertyName, AbstractComponent component){
        validations.add(new VaadinValidationNotification(propertyName, component,  {validationObserver}))
    }

    def startObservingErrors(){
        validationObserver = new SimpleValidationObserverImp()
        ApplicationValidationNotifier.addObserver(this.validationObserver)
    }

    def finishObservingErrors(){
        validations.each {it.applyValidation()}
        ApplicationValidationNotifier.removeObserver(this.validationObserver)
        validationObserver = null
    }

}
