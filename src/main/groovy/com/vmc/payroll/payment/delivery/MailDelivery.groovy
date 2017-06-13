package com.vmc.payroll.payment.delivery

import com.google.gwt.core.shared.impl.InternalPreconditions
import com.vmc.payroll.Employee
import com.vmc.payroll.payment.delivery.api.PaymentDelivery
import com.vmc.validationNotification.builder.BuilderAwareness
import com.vmc.validationNotification.builder.imp.GenericBuilder

import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation
import static com.vmc.validationNotification.ApplicationValidationNotifier.issueError

class MailDelivery implements PaymentDelivery, BuilderAwareness{

    private Employee employee
    String address

    static MailDelivery newPaymentDelivery(Employee employee, String address){
        return new GenericBuilder(MailDelivery).withEmployee(employee).withAddress(address).build()
    }

    //Should be used by builder only
    protected MailDelivery() {
        //Available only for reflection magic
        invalidForBuilder()
    }

    protected MailDelivery(Employee anEmployee, String anAddress) {
        InternalPreconditions.checkArgument(anEmployee != null, "Did you miss passing my employee?")
        executeNamedValidation("Validate new MailDelivery", {
            this.employee = anEmployee
            setAddress(anAddress)
        })
    }

    @Override
    Employee getEmployee() {
        return employee
    }

    void setAddress(String anAddress) {
        anAddress ? this.@address = anAddress : issueError(this, [name: "address"], "payroll.mail.delivery.address.mandatory")
    }
}