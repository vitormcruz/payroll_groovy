package com.vmc.payroll.domain.payment.delivery

import com.vmc.payroll.domain.payment.delivery.api.PaymentDelivery
import com.vmc.validationNotification.objectCreation.ConstructorValidator

import static com.google.common.base.Preconditions.checkArgument
import static com.vmc.validationNotification.Validate.validate

class Mail implements PaymentDelivery{

    protected employee
    String address

    static Mail newPaymentDelivery(employee, String address){
        return validate(Mail, {new Mail(employee, address)})
    }

    //For reflection magic only
    Mail() {
    }

    Mail(anEmployee, String anAddress) {
        def constructorValidator = new ConstructorValidator()
        initialize(anEmployee, anAddress)
        constructorValidator.validateConstruction()
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
