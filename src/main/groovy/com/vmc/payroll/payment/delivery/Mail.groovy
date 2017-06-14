package com.vmc.payroll.payment.delivery

import com.google.gwt.core.shared.impl.InternalPreconditions
import com.vmc.payroll.Employee
import com.vmc.payroll.payment.delivery.api.PaymentDelivery
import com.vmc.validationNotification.builder.BuilderAwareness
import com.vmc.validationNotification.builder.imp.GenericBuilder

import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation

class Mail implements PaymentDelivery, BuilderAwareness{

    private Employee employee
    String address

    static Mail newPaymentDelivery(Employee employee, String address){
        return new GenericBuilder(Mail).withEmployee(employee).withAddress(address).build()
    }

    //Should be used by builder only
    protected Mail() {
        //Available only for reflection magic
        invalidForBuilder()
    }

    protected Mail(Employee anEmployee, String anAddress) {
        InternalPreconditions.checkArgument(anEmployee != null, "Did you miss passing my employee?")
        executeNamedValidation("Validate new Mail", {
            this.employee = anEmployee
            setAddress(anAddress)
        })
    }

    @Override
    Employee getEmployee() {
        return employee
    }

    void setAddress(String anAddress) {
        anAddress ? this.@address = anAddress : issueError("payroll.mail.delivery.address.mandatory", [property: "address"])
    }
}
