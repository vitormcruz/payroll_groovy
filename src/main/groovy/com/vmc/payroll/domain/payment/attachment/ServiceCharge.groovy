package com.vmc.payroll.domain.payment.attachment

import com.vmc.payroll.domain.payment.attachment.api.UnionCharge
import org.joda.time.DateTime

import static com.vmc.validationNotification.Validate.validate
import static com.vmc.validationNotification.Validate.validateNewObject

class ServiceCharge implements UnionCharge{

    protected DateTime date
    protected amount

    static ServiceCharge newServiceCharge(DateTime date, amount){
        return validateNewObject(ServiceCharge, {new ServiceCharge(date, amount)})
    }

    //For reflection magic only
    ServiceCharge() {
    }

    ServiceCharge(DateTime date, amount) {
        validate {initialize(date, amount)}
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
