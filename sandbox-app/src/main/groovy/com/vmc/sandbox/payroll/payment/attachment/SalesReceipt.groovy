package com.vmc.sandbox.payroll.payment.attachment

import com.vmc.sandbox.validationNotification.builder.BuilderAwareness
import com.vmc.sandbox.validationNotification.builder.imp.GenericBuilder
import org.joda.time.DateTime

import static com.vmc.sandbox.validationNotification.ApplicationValidationNotifier.executeNamedValidation
import static com.vmc.sandbox.validationNotification.ApplicationValidationNotifier.issueError

class SalesReceipt implements PaymentAttachment, BuilderAwareness{

    private DateTime date
    private amount

    SalesReceipt() {
        //Available only for reflection magic
        invalidForBuilder()
    }

    //Should be used by builder only
    protected SalesReceipt(DateTime date, amount) {
        executeNamedValidation("Validate new SalesReceipt", {
            date != null? this.date = date : issueError(this, [:], "payroll.salesreceipt.date.required")
            amount != null? this.amount = amount : issueError(this, [:], "payroll.salesreceipt.amount.required")
        })
    }

    public static SalesReceipt newSalesReceipt(DateTime date, amount){
        return new GenericBuilder(SalesReceipt).withDate(date).withAmount(amount).build()
    }

    DateTime getDate() {
        return date
    }

    Integer getAmount() {
        return amount
    }
}
