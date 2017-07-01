package com.vmc.payroll.external.presentation.vaadin

import com.vaadin.ui.*
import com.vmc.concurrency.api.ModelSnapshot
import com.vmc.payroll.Employee
import com.vmc.payroll.api.Repository
import com.vmc.payroll.external.config.ServiceLocator
import com.vmc.payroll.payment.delivery.Mail
import com.vmc.payroll.payment.type.Monthly
import com.vmc.validationNotification.builder.imp.GenericBuilder

class EmployeeListView extends VerticalLayout {

    private ModelSnapshot modelSnapshot = ServiceLocator.instance.modelSnapshot()
    private Repository<Employee> employeeRepository = ServiceLocator.instance.employeeRepository()
    private FormLayout searchForm
    private Grid grid

    EmployeeListView() {
        employeeRepository.add(new GenericBuilder(Employee).withName("Sofia").withAddress("Street 1").withEmail("sofia@bla.com")
                                    .withPaimentType(Monthly, 2000).withPaymentDelivery(Mail, "Street 1").build())

        employeeRepository.add(new GenericBuilder(Employee).withName("Heloísa").withAddress("Street 1").withEmail("heloisa@bla.com")
                                    .withPaimentType(Monthly, 2000).withPaymentDelivery(Mail, "Street 1").build())

        modelSnapshot.save()

        searchForm = createSearchForm()
        grid = createResultingGrid()
        addComponent(searchForm)
        addComponent(grid)
        setSizeFull()

    }

    public FormLayout createSearchForm() {
        return new FormLayout().with {
            it.setSizeFull()
            it.addComponent(new TextField("Name: "))
            it.addComponent(new TextField("Address: "))
            it.addComponent(new Button("Search").with{
                it.addClickListener { Notification.show("Alert", "To be implemented", Notification.Type.ERROR_MESSAGE) }
                it
            })
            it
        }
    }

    public Grid createResultingGrid() {
        return new Grid<Employee>().with {
            it.setItems(employeeRepository)
            it.addColumn({it.getName()}).setCaption("Name")
            it.addColumn({it.getAddress()}).setCaption("Address")
            it.addColumn({it.getPaymentType().getClass().getSimpleName()}).setCaption("Payment Type")
            it
        }
    }
}
