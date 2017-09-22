package com.vmc.payroll.external.presentation.vaadin.view.employee

import com.vaadin.ui.*
import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository
import org.apache.commons.lang.StringUtils

class EmployeeListView extends VerticalLayout{
    private Repository<Employee> employeeRepository
    private Closure prepareNewEmployee
    private searchForm
    private Grid grid
    TextField nameField

    EmployeeListView(Repository<Employee> employeeRepository, Closure prepareNewEmployee) {
        this.employeeRepository = employeeRepository
        this.prepareNewEmployee = prepareNewEmployee
        searchForm = createSearchForm()
        grid = createResultingGrid()
        addComponent(searchForm)
        addComponent(grid)
    }

    def createSearchForm() {
        return new HorizontalLayout().with {
            nameField = new TextField("Name: ")
            it.addComponent(nameField)
            def searchButton = new Button("Search", {
                grid.setItems(employeeRepository.findAll {StringUtils.containsIgnoreCase(it.name, nameField.getValue())})
            } as Button.ClickListener)
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
