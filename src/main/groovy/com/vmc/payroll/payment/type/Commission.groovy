package com.vmc.payroll.payment.type

import com.vmc.payroll.Employee
import com.vmc.payroll.payment.attachment.api.PaymentAttachment
import com.vmc.payroll.payment.attachment.SalesReceipt
import com.vmc.validationNotification.builder.imp.GenericBuilder

import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation
import static com.vmc.validationNotification.ApplicationValidationNotifier.issueError

class Commission extends Monthly{

    Integer commissionRate

    static Commission newPaymentType(Employee employee, Integer salary, Integer commissionRate) {
        return new GenericBuilder(Commission).withEmployee(employee).withSalary(salary).withCommissionRate(commissionRate).build()
    }

    //Should be used by builder only
    protected Commission() {    }

    protected Commission(Employee employee, Integer aSalary, Integer aCommissionRate) {
        super(employee, aSalary)
        executeNamedValidation("Validate new Hourly Payment", {
            setCommissionRate(aCommissionRate)
        })
    }

    void setCommissionRate(Integer aCommissionRate) {
        def context = [name:"commissionRate"]
        if (aCommissionRate == null) {
            issueError(this, context, "payroll.employee.commisionpayment.commissionrate.mandatory")
        } else if (aCommissionRate < 1) {
            issueError(this, context, "payroll.employee.commisionpayment.commissionrate.mustbe.positive.integer")
        } else {
            this.@commissionRate = aCommissionRate
        }
    }

    @Override
    void addPaymentAttachment(PaymentAttachment paymentAttachment) {
        if(!(paymentAttachment instanceof SalesReceipt)){
            issueError(this, [:], "employee.payment.commission.sales.receipt.payment.info.only")
            return
        }

        this.@paymentAttachments.add(paymentAttachment)
    }
}
