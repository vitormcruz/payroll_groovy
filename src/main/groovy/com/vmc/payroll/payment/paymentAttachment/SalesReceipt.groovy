package com.vmc.payroll.payment.paymentAttachment

import com.vmc.payroll.payment.paymentAttachment.api.WorkDoneProof
import com.vmc.validationNotification.api.ConstructorValidator
import org.joda.time.DateTime

import static com.vmc.validationNotification.Validate.validate

class SalesReceipt implements WorkDoneProof{

    private DateTime date
    private amount

    static SalesReceipt newSalesReceipt(DateTime date, amount){
        return validate(SalesReceipt, {new SalesReceipt(date, amount)})
    }

    SalesReceipt() {
    }

    SalesReceipt(DateTime date, amount) {
        def constructorValidator = new ConstructorValidator()
        prepareConstructor(date, amount)
        constructorValidator.validateConstruction()
    }

    void prepareConstructor(DateTime date, amount) {
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
