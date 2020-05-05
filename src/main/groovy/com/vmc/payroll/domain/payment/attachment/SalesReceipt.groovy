package com.vmc.payroll.domain.payment.attachment

import com.vmc.payroll.domain.payment.attachment.api.WorkDoneProof

import java.time.LocalDateTime

import static com.vmc.validationNotification.Validation.validate
import static com.vmc.validationNotification.Validation.validateNewObject

class SalesReceipt implements WorkDoneProof{

    protected LocalDateTime date
    protected amount

    static SalesReceipt newSalesReceipt(LocalDateTime date, amount){
        return validateNewObject(SalesReceipt, {new SalesReceipt(date, amount)})
    }

    //For reflection magic only
    SalesReceipt() {
    }

    SalesReceipt(LocalDateTime date, amount) {
        validate {initialize(date, amount)}
    }

    void initialize(LocalDateTime date, amount) {
        date != null ? this.@date = date : issueError("payroll.salesreceipt.date.required", [property: "date"])
        amount != null ? this.@amount = amount : issueError("payroll.salesreceipt.amount.required", [property: "amount"])
    }

    LocalDateTime getDate() {
        return date
    }

    Integer getAmount() {
        return amount
    }
}
