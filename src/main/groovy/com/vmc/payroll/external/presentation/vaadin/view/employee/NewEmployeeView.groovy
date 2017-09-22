package com.vmc.payroll.external.presentation.vaadin.view.employee

import com.vaadin.data.Binder
import com.vaadin.ui.Button
import com.vaadin.ui.FormLayout
import com.vaadin.ui.Notification
import com.vaadin.ui.VerticalLayout
import com.vmc.concurrency.api.ModelSnapshot
import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository
import com.vmc.payroll.domain.payment.delivery.api.PaymentDelivery
import com.vmc.payroll.domain.payment.type.api.PaymentType
import com.vmc.payroll.domain.unionAssociation.api.UnionAssociation
import com.vmc.validationNotification.vaadin.BinderDecoratorForValidationNotification

import static com.vaadin.ui.Notification.show

class NewEmployeeView extends VerticalLayout{
    private Repository<Employee> employeeRepository
    private Closure cancelNewEmployee
    private BinderDecoratorForValidationNotification binder
    private ModelSnapshot modelSnapshot

    NewEmployeeView(Repository<Employee> employeeRepository, ModelSnapshot modelSnapshot, Closure cancelNewEmployee) {
        this.employeeRepository = employeeRepository
        this.modelSnapshot = modelSnapshot
        this.cancelNewEmployee = cancelNewEmployee
        this.binder = new BinderDecoratorForValidationNotification(new Binder<VaadinEmployeeDTO>(VaadinEmployeeDTO))
        addComponent(createNewForm())
    }

    def createNewForm() {
        return new FormLayout().with {FormLayout form ->
            form.setSizeFull()
            form.addComponents(*Employee.myVaadinComponents(binder))
            form.addComponents(*PaymentType.myVaadinComponents(binder))
            form.addComponents(*PaymentDelivery.myVaadinComponents(binder))
            form.addComponents(*UnionAssociation.myVaadinComponents(binder))
            form.addComponent(new Button("Save", {saveNewEmployee()} as Button.ClickListener))
            form.addComponent(new Button("Cancel", cancelNewEmployee as Button.ClickListener))
            form
        }
    }

    public void saveNewEmployee() {
        def employeeDTO = new VaadinEmployeeDTO()
        binder.writeBean(employeeDTO)
        binder.validateExecution {
            employeeDTO.toEntity().buildAndDo({
                                                         employeeRepository.add(it)
                                                         modelSnapshot.save()
                                                         cancelNewEmployee.call()
                                                     },
                                                     {show("Could not Create a new Employee", Notification.Type.ERROR_MESSAGE)})

        }
    }
}
