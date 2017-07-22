package com.vmc.payroll.payment.type

import com.vmc.payroll.payment.workEvent.SalesReceipt
import com.vmc.payroll.payment.workEvent.api.WorkDoneProof
import com.vmc.validationNotification.builder.GenericBuilder

import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation

class Commission extends Monthly{

    Integer commissionRate

    static Commission newPaymentType(employee, Integer salary, Integer commissionRate) {
        return new GenericBuilder(Commission).withEmployee(employee).withSalary(salary).withCommissionRate(commissionRate).build()
    }

    //Should be used by builder only
    protected Commission() {    }

    protected Commission(employee, Integer aSalary, Integer aCommissionRate) {
        super(employee, aSalary)
        executeNamedValidation("Validate new Hourly Payment", {
            setCommissionRate(aCommissionRate)
        })
    }

    void setCommissionRate(Integer aCommissionRate) {
        def context = [property:"commissionRate"]
        if (aCommissionRate == null) {
            issueError("The commission rate is required", context)
        } else if (aCommissionRate < 1) {
            issueError("The commission rate must be a positive integer", context)
        } else {
            this.@commissionRate = aCommissionRate
        }
    }

    @Override
    void addPaymentAttachment(WorkDoneProof paymentAttachment) {
        paymentAttachment instanceof SalesReceipt ? this.@paymentAttachments.add(paymentAttachment):
                                                    {throw new IllegalArgumentException("Non Sales Receipt payment attachment was provided to a commission payment type.")}()
    }
}
