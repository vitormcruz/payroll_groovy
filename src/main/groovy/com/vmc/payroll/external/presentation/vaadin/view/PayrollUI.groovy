package com.vmc.payroll.external.presentation.vaadin.view

import com.vaadin.annotations.Theme
import com.vaadin.annotations.Title
import com.vaadin.server.VaadinRequest
import com.vaadin.ui.UI
import com.vaadin.ui.VerticalLayout
import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository
import com.vmc.payroll.external.config.ServiceLocator
import com.vmc.payroll.external.presentation.vaadin.view.employee.EmployeeView
import com.vmc.userModel.api.UserModel

@Title("Payroll")
@Theme("valo")
class PayrollUI extends UI {

    private Repository<Employee> employeeRepository = ServiceLocator.instance.employeeRepository
    private UserModel modelSnapshot = ServiceLocator.instance.modelSnapshot

    protected void init(VaadinRequest request) {
        def mainContent = new VerticalLayout().with {
            it.setMargin(false)
            def employeeListView = new EmployeeView(employeeRepository, modelSnapshot)
            it.addComponent(employeeListView)
            it
        }

        setContent(mainContent)

    }
}
