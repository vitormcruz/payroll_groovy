package com.vmc.payroll.external.presentation.vaadin.view

import com.vaadin.ui.Button
import com.vaadin.ui.ComboBox
import com.vaadin.ui.FormLayout
import com.vaadin.ui.Notification
import com.vaadin.ui.RadioButtonGroup
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import com.vmc.payroll.api.EmployeeRepository
import com.vmc.payroll.payment.type.api.PaymentType

class NewEmployeeView extends VerticalLayout{
    private EmployeeRepository employeeRepository
    private Closure cancelNewEmployee
    private newForm

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
            it.addComponent(new ComboBox<PaymentType>("Select a payment type: ").with {
                it.setItems(employeeRepository.getAllPaymentTypes().collect {it.simpleName})
                it.setRequiredIndicatorVisible(true)
                it
            })

            it.addComponent(new RadioButtonGroup<String>("Is a Union Member").with {
                it.setItems(["Yes", "No"])
                it.setValue("Yes")
                it
            })

            it.addComponent(new Button("Save", {Notification.show("new employee", "to be implemented", Notification.Type.HUMANIZED_MESSAGE)} as Button.ClickListener))
            it.addComponent(new Button("Cancel", cancelNewEmployee as Button.ClickListener))
            it
        }
    }



}
