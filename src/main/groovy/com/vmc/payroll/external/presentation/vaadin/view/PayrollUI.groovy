package com.vmc.payroll.external.presentation.vaadin.view

import com.vaadin.annotations.Theme
import com.vaadin.annotations.Title
import com.vaadin.server.VaadinRequest
import com.vaadin.ui.UI
import com.vaadin.ui.VerticalLayout
import com.vmc.payroll.api.EmployeeRepository
import com.vmc.payroll.external.config.ServiceLocator
import com.vmc.payroll.external.presentation.vaadin.view.employee.EmployeeView

@Title("Payroll")
@Theme("valo")
class PayrollUI extends UI {

    private EmployeeRepository employeeRepository = ServiceLocator.instance.employeeRepository()

    protected void init(VaadinRequest request) {
        def mainContent = new VerticalLayout().with {
            it.setMargin(false)
            def employeeListView = new EmployeeView(employeeRepository)
            it.addComponent(employeeListView)
            it
        }

        setContent(mainContent)

    }
}
