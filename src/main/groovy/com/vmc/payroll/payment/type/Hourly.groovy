package com.vmc.payroll.payment.type

import com.vmc.payroll.payment.paymentAttachment.TimeCard
import com.vmc.payroll.payment.paymentAttachment.api.WorkDoneProof
import com.vmc.validationNotification.api.ConstructorValidator

import static com.vmc.validationNotification.Validate.validate

class Hourly extends GenericPaymentType {

    Integer hourRate

    static Hourly newPaymentType(employee, Integer hourRate) {
        return validate(Hourly, {new Hourly(employee, hourRate)})
    }

    Hourly() {
    }

    Hourly(employee, Integer aHourRate) {
        def constructorValidator = new ConstructorValidator()
        prepareConstructor(employee, aHourRate)
        constructorValidator.validateConstruction()
    }

    void prepareConstructor(Object anEmployee, Integer aHourRate) {
        super.prepareConstructor(anEmployee)
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
