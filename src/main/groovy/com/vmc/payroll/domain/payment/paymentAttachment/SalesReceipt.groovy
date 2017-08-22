package com.vmc.payroll.domain.payment.paymentAttachment

import com.vmc.payroll.domain.payment.paymentAttachment.api.WorkDoneProof
import com.vmc.validationNotification.objectCreation.ConstructorValidator
import org.joda.time.DateTime

import static com.vmc.validationNotification.Validate.validate

class SalesReceipt implements WorkDoneProof{

    protected DateTime date
    protected amount

    static SalesReceipt newSalesReceipt(DateTime date, amount){
        return validate(SalesReceipt, {new SalesReceipt(date, amount)})
    }

    //For reflection magic only
    SalesReceipt() {
    }

    SalesReceipt(DateTime date, amount) {
        def constructorValidator = new ConstructorValidator()
        initialize(date, amount)
        constructorValidator.validateConstruction()
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
