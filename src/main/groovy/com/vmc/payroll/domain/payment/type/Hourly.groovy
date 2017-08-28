package com.vmc.payroll.domain.payment.type

import com.vmc.payroll.domain.payment.attachment.TimeCard
import com.vmc.payroll.domain.payment.attachment.api.WorkDoneProof

import static com.vmc.validationNotification.Validation.validate
import static com.vmc.validationNotification.Validation.validateNewObject

class Hourly extends GenericPaymentType {

    Integer hourRate

    static Hourly newPaymentType(employee, Integer hourRate) {
        return validateNewObject(Hourly, {new Hourly(employee, hourRate)})
    }

    //For reflection magic only
    Hourly() {
    }

    Hourly(employee, Integer aHourRate) {
        validate {initialize(employee, aHourRate)}
    }

    void initialize(Object anEmployee, Integer aHourRate) {
        super.initialize(anEmployee)
        setHourRate(aHourRate)
    }

    void setHourRate(Integer aHourRate) {
        def context = [property:"hourRate"]
        if (aHourRate == null) {
            issueError("payroll.employee.hourlypayment.hourRate.mandatory", context)
        } else if (aHourRate < 1) {
            issueError("payroll.employee.hourlypayment.hourRate.mustbe.positive.integer", context)
        } else {
            this.hourRate = aHourRate
        }
    }

    @Override
    void addPaymentAttachment(WorkDoneProof paymentAttachment) {
        paymentAttachment instanceof TimeCard ? this.@paymentAttachments.add(paymentAttachment) :
                                                {throw new IllegalArgumentException("Non Time Card payment attachment was provided to a hourly payment type.")}()
    }
}
