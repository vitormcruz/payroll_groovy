package com.vmc.payroll.payment.type

import com.vmc.payroll.Employee
import com.vmc.payroll.payment.attachment.api.PaymentAttachment
import com.vmc.payroll.payment.attachment.TimeCard
import com.vmc.payroll.payment.type.api.GenericPaymentType
import com.vmc.validationNotification.builder.BuilderAwareness
import com.vmc.validationNotification.builder.imp.GenericBuilder

import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation
import static com.vmc.validationNotification.ApplicationValidationNotifier.issueError

class Hourly extends GenericPaymentType implements BuilderAwareness{

    Integer hourRate

    static Hourly newPaymentType(Employee employee, Integer hourRate) {
        return new GenericBuilder(Hourly).withEmployee(employee).withHourRate(hourRate).build()
    }

    //Should be used by builder only
    private Hourly() {
        super()
        //Available only for reflection magic
        invalidForBuilder()
    }

    protected Hourly(Employee employee, Integer aHourRate) {
        super(employee)
        executeNamedValidation("Validate new Hourly Payment", {
            setHourRate(aHourRate)
        })
    }

    void setHourRate(Integer aHourRate) {
        def context = [name:"hourRate"]
        if (aHourRate == null) {
            issueError(this, context, "payroll.employee.hourlypayment.hourRate.mandatory")
        } else if (aHourRate < 1) {
            issueError(this, context, "payroll.employee.hourlypayment.hourRate.mustbe.positive.integer")
        } else {
            this.hourRate = aHourRate
        }
    }

    @Override
    void addPaymentAttachment(PaymentAttachment paymentAttachment) {
        if(!(paymentAttachment instanceof TimeCard)){
            issueError(this, [:], "employee.payment.hourly.time.card.payment.info.only")
            return
        }

        this.@paymentAttachments.add(paymentAttachment)
    }
}
