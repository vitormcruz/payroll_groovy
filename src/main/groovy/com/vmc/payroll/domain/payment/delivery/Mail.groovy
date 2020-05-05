package com.vmc.payroll.domain.payment.delivery


import com.vmc.payroll.domain.payment.delivery.api.PaymentDelivery

import static com.google.common.base.Preconditions.checkArgument
import static com.vmc.validationNotification.Validation.validate
import static com.vmc.validationNotification.Validation.validateNewObject

class Mail implements PaymentDelivery{

    protected employee
    String address

    static Mail newPaymentDelivery(employee, String address){
        return validateNewObject(Mail, {new Mail(employee, address)})
    }

    //For reflection magic only
    Mail() {
    }

    Mail(anEmployee, String anAddress) {
        validate {initialize(anEmployee, anAddress)}
    }

    void initialize(anEmployee, String anAddress) {
        checkArgument(anEmployee != null, "Did you miss passing my employee?")
        this.employee = anEmployee
        setAddress(anAddress)
    }

    @Override
    def getEmployee() {
        return employee
    }

    void setAddress(String anAddress) {
        anAddress ? this.@address = anAddress : issueError("The address for mail delivery is required", [property: "address"])
    }
}
