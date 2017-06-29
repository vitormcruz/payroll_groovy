package com.vmc.payroll.external.presentation.vaadin.view.components

import com.vaadin.data.HasValue
import com.vaadin.ui.ComboBox
import com.vmc.payroll.payment.delivery.api.PaymentDelivery


class DynamicComboBox {

    private Collection comboData
    private Map componentesByDomainElements
    private defaultElement
    private String comboLabel
    ComboBox comboBox

    DynamicComboBox(String comboLabel, Collection domainElements, defaultElement) {
        this.comboLabel = comboLabel
        this.comboData = domainElements
        this.defaultElement = defaultElement
        componentesByDomainElements = comboData.collectEntries{ [(it), it.myVaadinComponents()] }
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
