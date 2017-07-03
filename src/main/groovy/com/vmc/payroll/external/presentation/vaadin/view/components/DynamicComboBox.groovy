package com.vmc.payroll.external.presentation.vaadin.view.components

import com.vaadin.data.Binder
import com.vaadin.data.HasValue
import com.vaadin.ui.ComboBox
import com.vmc.payroll.payment.delivery.api.PaymentDelivery


class DynamicComboBox {

    private String comboLabel
    private Collection comboData
    private defaultElement
    private Map componentesByDomainElements
    ComboBox comboBox

    DynamicComboBox(String comboLabel, Collection domainElements, defaultElement, Binder binder) {
        this.comboLabel = comboLabel
        this.comboData = domainElements
        this.defaultElement = defaultElement
        componentesByDomainElements = comboData.collectEntries{ [(it), it.myVaadinComponents(binder)] }
        comboBox = createComboBox()
    }

    def addMeTo(layout) {
        layout.addComponent(comboBox)
        createDynamicDataInto(layout)
        comboBox.setValue(defaultElement)
    }

    ComboBox getComboBox() {
        return comboBox
    }

    ComboBox<PaymentDelivery> createComboBox() {
        def comboBox = new ComboBox(comboLabel).with {
            it.setItems(comboData)
            it.addValueChangeListener({ event -> changeComboValue(event) })
            it.setRequiredIndicatorVisible(true)
            it
        }
        return comboBox
    }

    public Map createDynamicDataInto(layout) {
        componentesByDomainElements.each {
            it.value.each { component -> component.setVisible(false) }
            layout.addComponents(*it.value)
        }
    }

    def changeComboValue(HasValue.ValueChangeEvent event) {
        componentesByDomainElements.get(event.getOldValue())?.each {it.setVisible(false)}
        componentesByDomainElements.get(event.getValue())?.each {it.setVisible(true)}
    }

}
