package com.vmc.payroll.payment.type

import com.vmc.payroll.payment.workEvent.SalesReceipt
import com.vmc.payroll.payment.workEvent.api.PaymentAttachment
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
            issueError("payroll.employee.commisionpayment.commissionrate.mandatory", context)
        } else if (aCommissionRate < 1) {
            issueError("payroll.employee.commisionpayment.commissionrate.mustbe.positive.integer", context)
        } else {
            this.@commissionRate = aCommissionRate
        }
    }

    @Override
    void addPaymentAttachment(PaymentAttachment paymentAttachment) {
        paymentAttachment instanceof SalesReceipt ? this.@paymentAttachments.add(paymentAttachment):
                                                    issueError("employee.payment.commission.sales.receipt.payment.info.only")
    }
}
