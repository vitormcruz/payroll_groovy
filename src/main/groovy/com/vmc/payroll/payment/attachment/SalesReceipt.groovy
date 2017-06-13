package com.vmc.payroll.payment.attachment

import com.vmc.payroll.payment.attachment.api.PaymentAttachment
import com.vmc.validationNotification.builder.BuilderAwareness
import com.vmc.validationNotification.builder.imp.GenericBuilder
import org.joda.time.DateTime

import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation
import static com.vmc.validationNotification.ApplicationValidationNotifier.issueError

class SalesReceipt implements PaymentAttachment, BuilderAwareness{

    private DateTime date
    private amount

    private SalesReceipt() {
        //Available only for reflection magic
        invalidForBuilder()
    }

    //Should be used by builder only
    protected SalesReceipt(DateTime date, amount) {
        executeNamedValidation("Validate new SalesReceipt", {
            date != null? this.@date = date : issueError(this, [name:"date"], "payroll.salesreceipt.date.required")
            amount != null? this.@amount = amount : issueError(this, [name:"amount"], "payroll.salesreceipt.amount.required")
        })
    }

    static SalesReceipt newSalesReceipt(DateTime date, amount){
        return new GenericBuilder(SalesReceipt).withDate(date).withAmount(amount).build()
    }

    DateTime getDate() {
        return date
    }

    Integer getAmount() {
        return amount
    }
}
