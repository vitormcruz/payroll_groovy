package com.vmc.payroll.payment.paymentAttachment

import com.vmc.payroll.payment.paymentAttachment.api.WorkDoneProof
import com.vmc.validationNotification.Validate
import org.joda.time.DateTime

import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation

class TimeCard implements WorkDoneProof{

    private DateTime date
    private Integer hours

    static TimeCard newTimeCard(DateTime date, Integer hours){
        return Validate.validate {new TimeCard(date, hours)}
    }

    /**
     * Should be used for reflection magic only
     */
    TimeCard() {}

    /**
     * Use newTimeCard instead, otherwise be careful as you can end up with an invalid object.
     */
    TimeCard(DateTime date, Integer hours) {
        executeNamedValidation("Validate new TimeCard", {
            date != null ? this.@date = date : issueError("payroll.timecard.date.required", [property:"date"])
            hours != null ? this.@hours = hours : issueError("payroll.timecard.hours.required", [property:"hours"])
        })
    }

    DateTime getDate() {
        return date
    }

    Integer getHours() {
        return hours
    }
}
