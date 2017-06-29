package com.vmc.payroll.external.presentation.vaadin.view

import com.vaadin.data.HasValue
import com.vaadin.ui.*
import com.vmc.payroll.api.EmployeeRepository
import com.vmc.payroll.payment.delivery.Paymaster
import com.vmc.payroll.payment.delivery.api.PaymentDelivery
import com.vmc.payroll.payment.type.Monthly
import com.vmc.payroll.payment.type.api.PaymentType

class NewEmployeeView extends VerticalLayout{
    public static final Class<Monthly> DEFAULT_NEW_EMPLOYEE_PAYMENT_TYPE = Monthly
    public static final Class<Monthly> DEFAULT_NEW_EMPLOYEE_PAYMENT_DELIVERY = Paymaster

    private EmployeeRepository employeeRepository
    private Closure cancelNewEmployee
    private FormLayout newForm
    private unionMembershipOption
    private Map componentesByPaymentType
    private Map componentesByPaymentDelivery

    NewEmployeeView(EmployeeRepository employeeRepository, Closure cancelNewEmployee) {
        this.employeeRepository = employeeRepository
        this.cancelNewEmployee = cancelNewEmployee
        newForm = createNewForm()
        addComponent(newForm)
    }

    def createNewForm() {
        return new FormLayout().with {
            it.setSizeFull()
            it.addComponent(new TextField("Name: ").with {it.setRequiredIndicatorVisible(true); it})
            it.addComponent(new TextField("Address: ").with {it.setRequiredIndicatorVisible(true); it})
            it.addComponent(new TextField("Email: ").with{ it.setRequiredIndicatorVisible(true); it})
            createPaymentTypesSection(it)
            createPaymentDeliverySection(it)
            createUnionMemberSection(it)
            it.addComponent(new Button("Save", {Notification.show("new employee", "to be implemented", Notification.Type.HUMANIZED_MESSAGE)} as Button.ClickListener))
            it.addComponent(new Button("Cancel", cancelNewEmployee as Button.ClickListener))
            it
        }
    }

    def createPaymentDeliverySection(form) {
        def paymentDeliveryComboBox = createPaymentDeliveryComboBox(form)
        def paymentDeliveryList = employeeRepository.getAllPaymentDelivery()
        componentesByPaymentDelivery = paymentDeliveryList.collectEntries{ [(it), it.myVaadinComponents()] }
        componentesByPaymentDelivery.each {
            it.value.each {component -> component.setVisible(false)}
            form.addComponents(*it.value)
        }
        paymentDeliveryComboBox.setValue(DEFAULT_NEW_EMPLOYEE_PAYMENT_DELIVERY)

    }

    ComboBox<PaymentDelivery> createPaymentDeliveryComboBox(form) {
        def paymentTypeComboBox = new ComboBox<PaymentDelivery>("Select a payment delivery: ").with {
            it.setItems(employeeRepository.getAllPaymentDelivery().collect { it })
            it.addValueChangeListener({ event -> changePaymentDelivery(event) })
            it.setItemCaptionGenerator({ it.simpleName })
            it.setRequiredIndicatorVisible(true)
            it
        }
        form.addComponent(paymentTypeComboBox)
        return paymentTypeComboBox
    }

    def changePaymentDelivery(HasValue.ValueChangeEvent<PaymentDelivery> event) {
        componentesByPaymentDelivery.get(event.getOldValue())?.each {it.setVisible(false)}
        componentesByPaymentDelivery.get(event.getValue())?.each {it.setVisible(true)}
    }

    def createPaymentTypesSection(form) {
        def paymentTypeComboBox = createPaymentTypeComboBox(form)
        def paymentTypes = employeeRepository.getAllPaymentTypes()
        componentesByPaymentType = paymentTypes.collectEntries{ [(it), it.myVaadinComponents()] }
        componentesByPaymentType.each {
            it.value.each {component -> component.setVisible(false)}
            form.addComponents(*it.value)
        }
        paymentTypeComboBox.setValue(DEFAULT_NEW_EMPLOYEE_PAYMENT_TYPE)
    }

    ComboBox<PaymentType> createPaymentTypeComboBox(form) {
        def paymentTypeComboBox = new ComboBox<PaymentType>("Select a payment type: ").with {
            it.setItems(employeeRepository.getAllPaymentTypes().collect { it })
            it.addValueChangeListener({ event -> changePaymentType(event) })
            it.setItemCaptionGenerator({ it.simpleName })
            it.setRequiredIndicatorVisible(true)
            it
        }
        form.addComponent(paymentTypeComboBox)
        return paymentTypeComboBox
    }

    def changePaymentType(HasValue.ValueChangeEvent<PaymentType> event) {
        componentesByPaymentType.get(event.getOldValue())?.each {it.setVisible(false)}
        componentesByPaymentType.get(event.getValue())?.each {it.setVisible(true)}
    }

    def createUnionMemberSection(FormLayout form) {
        createIsUnionMemberOption(form)
        createUnionMembershipSection(form)
    }

    def RadioButtonGroup<String> createIsUnionMemberOption(form) {
        form.addComponent(new RadioButtonGroup<String>("Is a Union Member").with {
            it.setItems(["Yes", "No"])
            it.setValue("No")
            it.addValueChangeListener({ event -> isUnionMember(event) })
            it
        })
    }

    def createUnionMembershipSection(form) {
        unionMembershipOption = new TextField("Union Membership Rate: ").with { it.setRequiredIndicatorVisible(true); it }
        unionMembershipOption.setVisible(false)
        form.addComponent(unionMembershipOption)
    }

    def isUnionMember(HasValue.ValueChangeEvent<String> event) {
        if(event.getValue() == "Yes" && event.getOldValue() == "No"){
            unionMembershipOption.setVisible(true)
        }else if(event.getValue() == "No" && event.getOldValue() == "Yes"){
            unionMembershipOption.setVisible(false)
        }
    }
}
