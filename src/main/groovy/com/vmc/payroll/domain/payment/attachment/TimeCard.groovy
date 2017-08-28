package com.vmc.payroll.domain.payment.attachment

import com.vmc.payroll.domain.payment.attachment.api.WorkDoneProof
import org.joda.time.DateTime

import static com.vmc.validationNotification.Validation.validate
import static com.vmc.validationNotification.Validation.validateNewObject

class TimeCard implements WorkDoneProof{

    protected DateTime date
    protected Integer hours

    static TimeCard newTimeCard(DateTime date, Integer hours){
        return validateNewObject(TimeCard, {new TimeCard(date, hours)})
    }

    //For reflection magic only
    TimeCard() {
    }

    TimeCard(DateTime date, Integer hours) {
        validate { initialize(date, hours) }
    }

    void initialize(DateTime date, Integer hours) {
        date != null ? this.@date = date : issueError("payroll.timecard.date.required", [property: "date"])
        hours != null ? this.@hours = hours : issueError("payroll.timecard.hours.required", [property: "hours"])
    }

    DateTime getDate() {
        return date
    }

    Integer getHours() {
        return hours
    }
}
