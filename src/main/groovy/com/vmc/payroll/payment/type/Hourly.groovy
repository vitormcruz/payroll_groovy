package com.vmc.payroll.payment.type

import com.vmc.payroll.payment.paymentAttachment.TimeCard
import com.vmc.payroll.payment.paymentAttachment.api.WorkDoneProof
import com.vmc.payroll.payment.type.api.GenericPaymentType

import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation
import static com.vmc.validationNotification.Validate.validate

class Hourly extends GenericPaymentType {

    Integer hourRate

    static Hourly newPaymentType(employee, Integer hourRate) {
        return validate {new Hourly(employee, hourRate)}
    }

    /**
     * Should be used for reflection magic only
     */
    Hourly() {}

    /**
     * Use newPaymentType instead, otherwise be careful as you can end up with an invalid object.
     */
    protected Hourly(employee, Integer aHourRate) {
        super(employee)
        executeNamedValidation("Validate new Hourly Payment", {
            setHourRate(aHourRate)
        })
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
