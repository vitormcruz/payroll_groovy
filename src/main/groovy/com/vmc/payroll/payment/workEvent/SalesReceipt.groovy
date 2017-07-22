package com.vmc.payroll.payment.workEvent

import com.vmc.payroll.payment.workEvent.api.WorkDoneProof
import org.joda.time.DateTime

import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation
import static com.vmc.validationNotification.Validate.validate

class SalesReceipt implements WorkDoneProof{

    private DateTime date
    private amount

    static SalesReceipt newSalesReceipt(DateTime date, amount){
        return validate {new SalesReceipt(date, amount)}
    }

    /**
     * Should be used for reflection magic only
     */
    SalesReceipt() {}


    /**
     * Use newSalesReceipt instead, otherwise be careful as you can end up with an invalid object.
     */
    SalesReceipt(DateTime date, amount) {
        executeNamedValidation("Validate new SalesReceipt", {
            date != null? this.@date = date : issueError("payroll.salesreceipt.date.required", [property:"date"])
            amount != null? this.@amount = amount : issueError("payroll.salesreceipt.amount.required", [property:"amount"])
        })
    }

    DateTime getDate() {
        return date
    }

    Integer getAmount() {
        return amount
    }
}
