package com.vmc.payroll.payment.workEvent

import com.vmc.payroll.payment.workEvent.api.PaymentAttachment
import com.vmc.validationNotification.builder.api.BuilderAwareness
import com.vmc.validationNotification.builder.GenericBuilder
import org.joda.time.DateTime

import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation

class TimeCard implements PaymentAttachment, BuilderAwareness{

    private DateTime date
    private Integer hours

    private TimeCard() {
        //Available only for reflection magic
        invalidForBuilder()
    }

    //Should be used by builder only
    protected TimeCard(DateTime date, Integer hours) {
        executeNamedValidation("Validate new TimeCard", {
            date != null ? this.@date = date : issueError("payroll.timecard.date.required", [property:"date"])
            hours != null ? this.@hours = hours : issueError("payroll.timecard.hours.required", [property:"hours"])
        })
    }

    static TimeCard newTimeCard(DateTime date, Integer hours){
        return new GenericBuilder(TimeCard).withDate(date).withHours(hours).build()
    }

    DateTime getDate() {
        return date
    }

    Integer getHours() {
        return hours
    }
}
