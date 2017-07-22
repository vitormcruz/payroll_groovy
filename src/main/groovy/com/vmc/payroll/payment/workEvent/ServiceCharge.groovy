package com.vmc.payroll.payment.workEvent

import com.vmc.payroll.payment.workEvent.api.UnionCharge
import com.vmc.validationNotification.builder.api.BuilderAwareness
import org.joda.time.DateTime

import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation
import static com.vmc.validationNotification.Validate.validate

class ServiceCharge implements UnionCharge, BuilderAwareness{

    private DateTime date
    private amount

    static ServiceCharge newServiceCharge(DateTime date, amount){
        return validate {new ServiceCharge(date, amount)}
    }

    /**
     * Should be used for reflection magic only
     */
    private ServiceCharge() {}

    /**
     * Use newServiceCharge instead, otherwise be careful as you can end up with an invalid object.
     */
    protected ServiceCharge(DateTime date, amount) {
        executeNamedValidation("Validate new ServiceCharge", {
            date != null ? this.@date = date : issueError("payroll.servicecharge.date.required", [property:"date"])
            amount != null ? this.@amount = amount : issueError("payroll.servicecharge.amount.required", [property:"amount"])
        })
    }

    DateTime getDate() {
        return date
    }

    Integer getAmount() {
        return amount
    }
}
