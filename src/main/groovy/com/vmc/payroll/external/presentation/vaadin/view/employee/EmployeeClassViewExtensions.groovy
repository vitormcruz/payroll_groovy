package com.vmc.payroll.external.presentation.vaadin.view.employee

import com.vaadin.ui.TextField
import com.vmc.payroll.domain.Employee
import com.vmc.validationNotification.vaadin.BinderDecoratorForValidationNotification

class EmployeeClassViewExtensions {

    static ArrayList myVaadinComponents(Employee employee, BinderDecoratorForValidationNotification binder) {
        return [
                new TextField("Name: ").with {
                    it.setRequiredIndicatorVisible(true)
                    binder.bind(it, "name")
                    binder.bindValidationFor(it, Employee, "name")
                    it
                },

                new TextField("Address: ").with {
                    it.setRequiredIndicatorVisible(true)
                    binder.bind(it, "address")
                    binder.bindValidationFor(it, Employee, "address")
                    it
                },

                new TextField("Email: ").with{
                    it.setRequiredIndicatorVisible(true)
                    binder.bind(it, "email")
                    binder.bindValidationFor(it, Employee, "email")
                    it
                }
        ]
    }

}
