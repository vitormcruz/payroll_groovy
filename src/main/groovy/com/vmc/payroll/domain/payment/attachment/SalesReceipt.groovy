package com.vmc.payroll.domain.payment.attachment

import com.vmc.payroll.domain.payment.attachment.api.WorkDoneProof
import org.joda.time.DateTime

import static com.vmc.validationNotification.Validate.validate
import static com.vmc.validationNotification.Validate.validateNewObject

class SalesReceipt implements WorkDoneProof{

    protected DateTime date
    protected amount

    static SalesReceipt newSalesReceipt(DateTime date, amount){
        return validateNewObject(SalesReceipt, {new SalesReceipt(date, amount)})
    }

    //For reflection magic only
    SalesReceipt() {
    }

    SalesReceipt(DateTime date, amount) {
        validate {initialize(date, amount)}
    }

    void initialize(DateTime date, amount) {
        date != null ? this.@date = date : issueError("payroll.salesreceipt.date.required", [property: "date"])
        amount != null ? this.@amount = amount : issueError("payroll.salesreceipt.amount.required", [property: "amount"])
    }

    DateTime getDate() {
        return date
    }

    Integer getAmount() {
        return amount
    }
}
