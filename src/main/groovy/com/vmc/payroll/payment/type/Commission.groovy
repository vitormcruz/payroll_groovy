package com.vmc.payroll.payment.type

import com.vmc.payroll.payment.paymentAttachment.SalesReceipt
import com.vmc.payroll.payment.paymentAttachment.api.WorkDoneProof

import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation
import static com.vmc.validationNotification.Validate.validate

class Commission extends Monthly{

    Integer commissionRate

    static Commission newPaymentType(employee, Integer salary, Integer commissionRate) {
        return validate {new Commission(employee, salary, commissionRate)}
    }

    /**
     * Should be used for reflection magic only
     */
    protected Commission() {}

    /**
     * Use newPaymentType instead, otherwise be careful as you can end up with an invalid object.
     */
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
                                                    {throw new IllegalArgumentException("Non Sales Receipt payment " +
                                                                                        "attachment was provided to a " +
                                                                                        "commission payment type.")}()
    }
}
