package com.vmc.payroll.domain.payment.type

import com.vmc.payroll.domain.payment.paymentAttachment.SalesReceipt
import com.vmc.payroll.domain.payment.paymentAttachment.api.WorkDoneProof
import com.vmc.validationNotification.objectCreation.ConstructorValidator

import static com.vmc.validationNotification.Validate.validate

class Commission extends Monthly{

    Integer commissionRate

    static Commission newPaymentType(employee, Integer salary, Integer commissionRate) {
        return validate(Commission, {new Commission(employee, salary, commissionRate)})
    }

    //For reflection magic only
    Commission() {
    }

    Commission(employee, Integer aSalary, Integer aCommissionRate) {
        def constructorValidator = new ConstructorValidator()
        initialize(employee, aSalary, aCommissionRate)
        constructorValidator.validateConstruction()
    }

    void initialize(Object anEmployee, Integer aSalary, Integer aCommissionRate) {
        super.initialize(anEmployee, aSalary)
        setCommissionRate(aCommissionRate)
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
