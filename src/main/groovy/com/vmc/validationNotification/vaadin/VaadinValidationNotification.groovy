package com.vmc.validationNotification.vaadin

import com.vaadin.server.UserError
import com.vaadin.ui.AbstractComponent
import com.vmc.validationNotification.api.SimpleValidationObserver

class VaadinValidationNotification {

    public static final String CONTEXT_TYPE = "property"
    private String propertyName
    private Closure<SimpleValidationObserver> validationObserverProvider
    private AbstractComponent component

    def VaadinValidationNotification(String propertyName, AbstractComponent component, Closure<Map> validationObserverProvider) {
        this.component = component
        this.validationObserverProvider = validationObserverProvider
        this.propertyName = propertyName
    }

    void applyValidation() {
        def errorsByContext = validationObserverProvider.call().errorsByContext
        def error = errorsByContext.get(CONTEXT_TYPE, propertyName)
        if (error != null){ component.setComponentError(new UserError(error)) }
    }
}
