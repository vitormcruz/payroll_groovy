package com.vmc.payroll.domain.payment.paymentAttachment

import com.vmc.payroll.domain.payment.paymentAttachment.api.UnionCharge
import com.vmc.validationNotification.objectCreation.ConstructorValidator
import org.joda.time.DateTime

import static com.vmc.validationNotification.Validate.validate

class ServiceCharge implements UnionCharge{

    protected DateTime date
    protected amount

    static ServiceCharge newServiceCharge(DateTime date, amount){
        return validate(ServiceCharge, {new ServiceCharge(date, amount)})
    }

    //For reflection magic only
    ServiceCharge() {
    }

    ServiceCharge(DateTime date, amount) {
        def constructorValidator = new ConstructorValidator()
        initialize(date, amount)
        constructorValidator.validateConstruction()
    }

    void initialize(DateTime date, amount) {
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
