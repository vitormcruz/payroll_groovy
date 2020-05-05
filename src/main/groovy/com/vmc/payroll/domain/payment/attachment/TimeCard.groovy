package com.vmc.payroll.domain.payment.attachment

import com.vmc.payroll.domain.payment.attachment.api.WorkDoneProof

import java.time.LocalDateTime

import static com.vmc.validationNotification.Validation.validate
import static com.vmc.validationNotification.Validation.validateNewObject

class TimeCard implements WorkDoneProof{

    protected LocalDateTime date
    protected Integer hours

    static TimeCard newTimeCard(LocalDateTime date, Integer hours){
        return validateNewObject(TimeCard, {new TimeCard(date, hours)})
    }

    //For reflection magic only
    TimeCard() {
    }

    TimeCard(LocalDateTime date, Integer hours) {
        validate { initialize(date, hours) }
    }

    void initialize(LocalDateTime date, Integer hours) {
        date != null ? this.@date = date : issueError("payroll.timecard.date.required", [property: "date"])
        hours != null ? this.@hours = hours : issueError("payroll.timecard.hours.required", [property: "hours"])
    }

    LocalDateTime getDate() {
        return date
    }

    Integer getHours() {
        return hours
    }
}
