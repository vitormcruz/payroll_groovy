package com.vmc.payroll.domain.payment.paymentAttachment

import com.vmc.payroll.domain.payment.paymentAttachment.api.UnionCharge
import com.vmc.validationNotification.objectCreation.ConstructorValidator
import org.joda.time.DateTime

import static com.vmc.validationNotification.Validate.validate

class ServiceCharge implements UnionCharge{

    private DateTime date
    private amount

    static ServiceCharge newServiceCharge(DateTime date, amount){
        return validate(ServiceCharge, {new ServiceCharge(date, amount)})
    }

    ServiceCharge() {
    }

    ServiceCharge(DateTime date, amount) {
        def constructorValidator = new ConstructorValidator()
        prepareConstructor(date, amount)
        constructorValidator.validateConstruction()
    }

    void prepareConstructor(DateTime date, amount) {
        date != null ? this.@date = date : issueError("payroll.servicecharge.date.required", [property: "date"])
        amount != null ? this.@amount = amount : issueError("payroll.servicecharge.amount.required", [property: "amount"])
    }

    DateTime getDate() {
        return date
    }

    Integer getAmount() {
        return amount
    }
}
