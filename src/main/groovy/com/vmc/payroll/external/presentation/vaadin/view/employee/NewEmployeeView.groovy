package com.vmc.payroll.external.presentation.vaadin.view.employee

import com.vaadin.data.HasValue
import com.vaadin.ui.*
import com.vmc.payroll.api.EmployeeRepository
import com.vmc.payroll.external.presentation.vaadin.view.components.DynamicComboBox
import com.vmc.payroll.payment.delivery.Paymaster
import com.vmc.payroll.payment.type.Monthly

class NewEmployeeView extends VerticalLayout{
    public static final Class<Monthly> DEFAULT_NEW_EMPLOYEE_PAYMENT_TYPE = Monthly
    public static final Class<Monthly> DEFAULT_NEW_EMPLOYEE_PAYMENT_DELIVERY = Paymaster

    private EmployeeRepository employeeRepository
    private Closure cancelNewEmployee
    private FormLayout newForm
    private unionMembershipOption
    private DynamicComboBox paymentTypeComboBox
    private DynamicComboBox paymentDeliveryComboBox

    NewEmployeeView(EmployeeRepository employeeRepository, Closure cancelNewEmployee) {
        this.employeeRepository = employeeRepository
        this.cancelNewEmployee = cancelNewEmployee
        paymentTypeComboBox = new DynamicComboBox("Select a payment type: ", employeeRepository.getAllPaymentTypes(), DEFAULT_NEW_EMPLOYEE_PAYMENT_TYPE)
        paymentTypeComboBox.comboBox.setItemCaptionGenerator({ it.simpleName })
        paymentDeliveryComboBox = new DynamicComboBox("Select a payment delivery: ", employeeRepository.getAllPaymentDelivery(), DEFAULT_NEW_EMPLOYEE_PAYMENT_DELIVERY)
        paymentDeliveryComboBox.comboBox.setItemCaptionGenerator({ it.simpleName })
        newForm = createNewForm()
        addComponent(newForm)
    }

    def createNewForm() {
        return new FormLayout().with {
            it.setSizeFull()
            it.addComponent(new TextField("Name: ").with {it.setRequiredIndicatorVisible(true); it})
            it.addComponent(new TextField("Address: ").with {it.setRequiredIndicatorVisible(true); it})
            it.addComponent(new TextField("Email: ").with{ it.setRequiredIndicatorVisible(true); it})
            paymentTypeComboBox.addMeTo(it)
            paymentDeliveryComboBox.addMeTo(it)
            createUnionMemberSection(it)
            it.addComponent(new Button("Save", {Notification.show("new employee", "to be implemented", Notification.Type.HUMANIZED_MESSAGE)} as Button.ClickListener))
            it.addComponent(new Button("Cancel", cancelNewEmployee as Button.ClickListener))
            it
        }
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
