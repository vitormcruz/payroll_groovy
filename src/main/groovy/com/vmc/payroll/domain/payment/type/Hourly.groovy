package com.vmc.payroll.domain.payment.type

import com.vmc.payroll.domain.payment.attachment.TimeCard
import com.vmc.payroll.domain.payment.attachment.api.WorkDoneProof
import com.vmc.validationNotification.objectCreation.ConstructorValidator

import static com.vmc.validationNotification.Validate.validate

class Hourly extends GenericPaymentType {

    Integer hourRate

    static Hourly newPaymentType(employee, Integer hourRate) {
        return validate(Hourly, {new Hourly(employee, hourRate)})
    }

    //For reflection magic only
    Hourly() {
    }

    Hourly(employee, Integer aHourRate) {
        def constructorValidator = new ConstructorValidator()
        initialize(employee, aHourRate)
        constructorValidator.validateConstruction()
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
