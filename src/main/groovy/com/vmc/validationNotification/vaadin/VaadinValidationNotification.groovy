package com.vmc.validationNotification.vaadin

import com.google.common.collect.Maps
import com.vaadin.server.UserError
import com.vaadin.ui.AbstractComponent
import com.vmc.validationNotification.api.SimpleValidationObserver

import static com.vmc.validationNotification.SimpleValidationObserverImp.issuerClassContextName

class VaadinValidationNotification {

    public static final String CONTEXT_TYPE = "property"

    private Class validationClass
    private String propertyName
    private AbstractComponent component
    private Closure<SimpleValidationObserver> validationObserverProvider

    def VaadinValidationNotification(Class validationClass, String propertyName, AbstractComponent component, Closure<Map> validationObserverProvider) {
        this.validationClass = validationClass
        this.propertyName = propertyName
        this.component = component
        this.validationObserverProvider = validationObserverProvider
    }

    void applyValidation() {
        def errorsByContext = validationObserverProvider.call().errorsByContext
        def errors = errorsByContext.get(Maps.immutableEntry(CONTEXT_TYPE, propertyName)).intersect(errorsByContext.get(Maps.immutableEntry(issuerClassContextName(), validationClass)))
        if (errors.size() > 1) throw new IllegalStateException("Validation of property should come up with one error only, but more were found: " + errors.join(", "))
        errors.isEmpty() ? component.setComponentError(null) : component.setComponentError(new UserError(errors.first()))
    }
}
