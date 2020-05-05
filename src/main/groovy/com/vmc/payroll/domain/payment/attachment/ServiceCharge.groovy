package com.vmc.payroll.domain.payment.attachment

import com.vmc.payroll.domain.payment.attachment.api.UnionCharge

import java.time.LocalDateTime

import static com.vmc.validationNotification.Validation.validate
import static com.vmc.validationNotification.Validation.validateNewObject

class ServiceCharge implements UnionCharge{

    protected LocalDateTime date
    protected amount

    static ServiceCharge newServiceCharge(LocalDateTime date, amount){
        return validateNewObject(ServiceCharge, {new ServiceCharge(date, amount)})
    }

    //For reflection magic only
    ServiceCharge() {
    }

    ServiceCharge(LocalDateTime date, amount) {
        validate {initialize(date, amount)}
    }

    void initialize(LocalDateTime date, amount) {
        date != null ? this.@date = date : issueError("payroll.servicecharge.date.required", [property: "date"])
        amount != null ? this.@amount = amount : issueError("payroll.servicecharge.amount.required", [property: "amount"])
    }

    LocalDateTime getDate() {
        return date
    }

    Integer getAmount() {
        return amount
    }
}
