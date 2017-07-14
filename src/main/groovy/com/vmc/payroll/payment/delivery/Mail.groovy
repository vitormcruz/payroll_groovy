package com.vmc.payroll.payment.delivery

import com.vmc.payroll.payment.delivery.api.PaymentDelivery
import com.vmc.validationNotification.builder.api.BuilderAwareness
import com.vmc.validationNotification.builder.GenericBuilder

import static com.google.common.base.Preconditions.checkArgument
import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation

class Mail implements PaymentDelivery, BuilderAwareness{

    private employee
    String address

    static Mail newPaymentDelivery(employee, String address){
        return new GenericBuilder(Mail).withEmployee(employee).withAddress(address).build()
    }

    //Should be used by builder only
    protected Mail() {
        //Available only for reflection magic
        invalidForBuilder()
    }

    protected Mail(anEmployee, String anAddress) {
        checkArgument(anEmployee != null, "Did you miss passing my employee?")
        executeNamedValidation("Validate new Mail", {
            this.employee = anEmployee
            setAddress(anAddress)
        })
    }

    @Override
    def getEmployee() {
        return employee
    }

    void setAddress(String anAddress) {
        anAddress ? this.@address = anAddress : issueError("The address for mail delivery is required", [property: "address"])
    }
}
