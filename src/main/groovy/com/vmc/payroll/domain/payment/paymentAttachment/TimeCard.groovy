package com.vmc.payroll.domain.payment.paymentAttachment

import com.vmc.payroll.domain.payment.paymentAttachment.api.WorkDoneProof
import com.vmc.validationNotification.Validate
import com.vmc.validationNotification.objectCreation.ConstructorValidator
import org.joda.time.DateTime

class TimeCard implements WorkDoneProof{

    protected DateTime date
    protected Integer hours

    static TimeCard newTimeCard(DateTime date, Integer hours){
        return Validate.validate(TimeCard, {new TimeCard(date, hours)})
    }

    //For reflection magic only
    TimeCard() {
    }

    TimeCard(DateTime date, Integer hours) {
        def constructorValidator = new ConstructorValidator()
        initialize(date, hours)
        constructorValidator.validateConstruction()
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
