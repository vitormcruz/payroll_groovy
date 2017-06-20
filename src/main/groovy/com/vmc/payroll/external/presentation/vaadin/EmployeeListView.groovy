package com.vmc.payroll.external.presentation.vaadin

import com.vaadin.ui.*
import com.vmc.payroll.Employee
import com.vmc.payroll.api.Repository
import com.vmc.payroll.external.config.ServiceLocator

class EmployeeListView extends VerticalLayout {

    private Repository<Employee> employeeRepository = ServiceLocator.instance.employeeRepository()
    private searchForm
    private Grid grid

    EmployeeListView() {
        searchForm = createSearchForm()
        grid = createResultingGrid()
        addComponent(searchForm)
        addComponent(grid)
        setSizeFull()
    }

    def createSearchForm() {
        return new HorizontalLayout().with {
            it.setSizeFull()
            it.addComponent(new TextField("Name: "))
            it.addComponent(new TextField("Address: "))
            def searchButton = new Button("Search", { Notification.show("Alert", "To be implemented", Notification.Type.ERROR_MESSAGE) } as Button.ClickListener)
            it.addComponent(searchButton)
            it.setComponentAlignment(searchButton, Alignment.BOTTOM_CENTER)
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
