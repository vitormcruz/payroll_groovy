package com.vmc.payroll.external.presentation.vaadin.view.components

import com.vaadin.data.HasValue
import com.vaadin.ui.ComboBox
import com.vmc.payroll.domain.payment.delivery.api.PaymentDelivery
import com.vmc.validationNotification.vaadin.BinderDecoratorForValidationNotification

//TODO revision this class
class DynamicComboBox {

    private String comboLabel
    private Collection comboData
    private defaultElement
    private Map componentesByDomainElements
    private components = new ArrayList()
    private itemCaptionGeneratorClosure
    private ComboBox comboBox
    private BinderDecoratorForValidationNotification binder

    DynamicComboBox(String comboLabel, Collection domainElements, defaultElement, BinderDecoratorForValidationNotification binder, itemCaptionGeneratorClosure) {
        this.comboLabel = comboLabel
        this.comboData = domainElements
        this.binder = binder
        this.defaultElement = defaultElement
        this.itemCaptionGeneratorClosure = itemCaptionGeneratorClosure
        componentesByDomainElements = comboData.collectEntries{ [(it), it.myVaadinComponents(binder)] }
        comboBox = createComboBox()
        components.add(comboBox)
        createDynamicDataInto()
        comboBox.setValue(defaultElement)
    }

    ComboBox getComboBox() {
        return comboBox
    }

    def getComponents() {
        return components
    }

    ComboBox<PaymentDelivery> createComboBox() {
        def comboBox = new ComboBox(comboLabel).with {
            it.setItems(comboData)
            it.addValueChangeListener({ event -> changeComboValue(event) })
            it.setRequiredIndicatorVisible(true)
            it.setItemCaptionGenerator(itemCaptionGeneratorClosure)
            it
        }
        return comboBox
    }

    public Map createDynamicDataInto() {
        componentesByDomainElements.each {
            it.value.each { component -> component.setVisible(false) }
            components.addAll(it.value)
        }
    }

    def changeComboValue(HasValue.ValueChangeEvent event) {
        componentesByDomainElements.get(event.getOldValue())?.each {it.setVisible(false); binder.disableBindingFor(it)}
        componentesByDomainElements.get(event.getValue())?.each {it.setVisible(true); binder.enableBindingFor(it)}
    }

}
