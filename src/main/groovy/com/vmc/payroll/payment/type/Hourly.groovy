package com.vmc.payroll.payment.type

import com.vmc.payroll.payment.type.api.GenericPaymentType
import com.vmc.payroll.payment.workEvent.TimeCard
import com.vmc.payroll.payment.workEvent.api.PaymentAttachment
import com.vmc.validationNotification.builder.BuilderAwareness
import com.vmc.validationNotification.builder.imp.GenericBuilder

import static com.vmc.validationNotification.imp.ApplicationValidationNotifier.executeNamedValidation

class Hourly extends GenericPaymentType implements BuilderAwareness{

    Integer hourRate

    static Hourly newPaymentType(employee, Integer hourRate) {
        return new GenericBuilder(Hourly).withEmployee(employee).withHourRate(hourRate).build()
    }

    //Should be used by builder only
    private Hourly() {
        super()
        //Available only for reflection magic
        invalidForBuilder()
    }

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
    void addPaymentAttachment(PaymentAttachment paymentAttachment) {
        paymentAttachment instanceof TimeCard ? this.@paymentAttachments.add(paymentAttachment) :
                                                issueError("employee.payment.hourly.time.card.payment.info.only")
    }
}
