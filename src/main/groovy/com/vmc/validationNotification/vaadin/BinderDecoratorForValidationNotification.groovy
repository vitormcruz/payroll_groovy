package com.vmc.validationNotification.vaadin

import com.vaadin.data.Binder
import com.vaadin.ui.AbstractComponent
import com.vmc.validationNotification.ApplicationValidationNotifier
import com.vmc.validationNotification.SimpleValidationObserverImp

class BinderDecoratorForValidationNotification {

    @Delegate
    private Binder binder
    private validationObserver
    private validations = []
    private disabledBindings = [:]

    BinderDecoratorForValidationNotification(Binder binder) {
        this.binder = binder
    }

    def bindValidationFor(AbstractComponent component, Class aClass, String propertyName){
        validations.add(new VaadinValidationNotification(aClass, propertyName, component,  {validationObserver}))
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

    def disableBindingFor(component){
        def bindings = binder.getBindings()
        def bindToDisable = bindings.find {it.field == component}
        if(bindToDisable == null) throw new IllegalArgumentException("")
        bindings.remove(bindToDisable)
        disabledBindings.put(component, bindToDisable)
    }

    def enableBindingFor(component){
        def bindingToEnable = disabledBindings.get(component)
        if(bindingToEnable == null ) return
        binder.getBindings().add(bindingToEnable)
    }

    void validateExecution(Closure<Void> aClosure) {
        startObservingErrors()
        aClosure()
        finishObservingErrors()
    }
}
