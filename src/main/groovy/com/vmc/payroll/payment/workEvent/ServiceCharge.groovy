package com.vmc.payroll.payment.workEvent

import com.vmc.payroll.payment.workEvent.api.UnionCharge
import com.vmc.validationNotification.builder.BuilderAwareness
import com.vmc.validationNotification.builder.imp.GenericBuilder
import org.joda.time.DateTime

import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation
import static com.vmc.validationNotification.ApplicationValidationNotifier.issueError

class ServiceCharge implements UnionCharge, BuilderAwareness{

    private DateTime date
    private amount

    private ServiceCharge() {
        //Available only for reflection magic
        invalidForBuilder()
    }

    //Should be used by builder only
    protected ServiceCharge(DateTime date, amount) {
        executeNamedValidation("Validate new ServiceCharge", {
            date != null ? this.@date = date : issueError(this, [name:"date"], "payroll.servicecharge.date.required")
            amount != null ? this.@amount = amount : issueError(this, [name:"amount"], "payroll.servicecharge.amount.required")
        })
    }

    static ServiceCharge newServiceCharge(DateTime date, amount){
        return new GenericBuilder(ServiceCharge).withDate(date).withAmount(amount).build()
    }

    DateTime getDate() {
        return date
    }

    Integer getAmount() {
        return amount
    }
}
