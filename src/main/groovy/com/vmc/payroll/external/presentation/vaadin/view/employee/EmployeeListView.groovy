package com.vmc.payroll.external.presentation.vaadin.view.employee

import com.vaadin.ui.*
import com.vmc.payroll.Employee
import com.vmc.payroll.api.EmployeeRepository

class EmployeeListView extends VerticalLayout{
    private EmployeeRepository employeeRepository
    private Closure prepareNewEmployee
    private searchForm
    private Grid grid

    EmployeeListView(EmployeeRepository employeeRepository, Closure prepareNewEmployee) {
        this.employeeRepository = employeeRepository
        this.prepareNewEmployee = prepareNewEmployee
        searchForm = createSearchForm()
        grid = createResultingGrid()
        addComponent(searchForm)
        addComponent(grid)
    }

    def createSearchForm() {
        return new HorizontalLayout().with {
            it.addComponent(new TextField("Name: "))
            def searchButton = new Button("Search", {Notification.show("search employee", "to be implemented", Notification.Type.HUMANIZED_MESSAGE)} as Button.ClickListener)
            it.addComponent(searchButton)
            it.setComponentAlignment(searchButton, Alignment.BOTTOM_CENTER)
            def newButton = new Button("New", prepareNewEmployee as Button.ClickListener)
            it.addComponent(newButton)
            it.setComponentAlignment(newButton, Alignment.BOTTOM_CENTER)
            it
        }
    }

    public Grid createResultingGrid() {
        return new Grid<Employee>().with {
            it.setItems(employeeRepository)
            it.addColumn({it.getName()}).setCaption("Name")
            it.addColumn({it.getAddress()}).setCaption("Address")
            it.addColumn({it.getPaymentType().getClass().getSimpleName()}).setCaption("Payment Type")
            it.setSizeFull()
            it
        }
    }
}
