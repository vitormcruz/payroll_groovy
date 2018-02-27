package com.vmc.payroll.external.presentation.vaadin.view.employee

import com.vaadin.ui.VerticalLayout
import com.vmc.concurrency.api.UserModelSnapshot
import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository

class EmployeeView extends VerticalLayout {

    private Repository<Employee> employeeRepository
    private UserModelSnapshot modelSnapshot

    EmployeeView(Repository<Employee> employeeRepository, UserModelSnapshot modelSnapshot) {
        this.employeeRepository = employeeRepository
        this.modelSnapshot = modelSnapshot
        setSizeFull()
        setMargin(false)
        loadList()
    }

    public void loadList() {
        removeAllComponents()
        addComponent(new EmployeeListView(employeeRepository, {loadNewEmployee()}))
    }

    void setSearchButtonListener(Closure searchButtonListener) {
        this.searchButtonListener = searchButtonListener
    }

    public void loadNewEmployee() {
        removeAllComponents()
        addComponent(new NewEmployeeView(employeeRepository, modelSnapshot, {loadList()}))
    }
}
