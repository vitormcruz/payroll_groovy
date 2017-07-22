package com.vmc.payroll.external.presentation.vaadin.view.employee

import com.vaadin.ui.VerticalLayout
import com.vmc.concurrency.api.ModelSnapshot
import com.vmc.payroll.api.EmployeeRepository

class EmployeeView extends VerticalLayout {

    private EmployeeRepository employeeRepository
    private ModelSnapshot modelSnapshot

    EmployeeView(EmployeeRepository employeeRepository, ModelSnapshot modelSnapshot) {
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