package com.vmc.payroll.payment.delivery

import com.vmc.payroll.payment.delivery.api.PaymentDelivery

import static com.google.common.base.Preconditions.checkArgument
import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation
import static com.vmc.validationNotification.Validate.validate

class Mail implements PaymentDelivery{

    private employee
    String address

    static Mail newPaymentDelivery(employee, String address){
        return validate {new Mail(employee, address)}
    }

    /**
     * Should be used for reflection magic only
     */
    protected Mail() {}

    /**
     * Use newPaymentDelivery instead, otherwise be careful as you can end up with an invalid object.
     */
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
