package com.vmc.payroll.payment.type

import com.vmc.payroll.payment.type.api.GenericPaymentType
import com.vmc.payroll.payment.workEvent.api.PaymentAttachment
import com.vmc.validationNotification.builder.api.BuilderAwareness
import com.vmc.validationNotification.builder.GenericBuilder

import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation

class Monthly extends GenericPaymentType implements BuilderAwareness{

    Integer salary

    static Monthly newPaymentType(employee, Integer salary){
        return new GenericBuilder(Monthly).withEmployee(employee).withSalary(salary).build()
    }

    //Should be used by builder only
    protected Monthly() {
        super()
        //Available only for reflection magic
        invalidForBuilder()
    }

    protected Monthly(employee, Integer aSalary) {
        super(employee)
        executeNamedValidation("Validate new Monhtly Payment", {
            setSalary(aSalary)
        })
    }

    void setSalary(Integer aSalary) {
        def context = [property: "salary"]
        if (aSalary == null) {
            issueError("payroll.employee.monthlypayment.salary.mandatory", context)
        } else if (aSalary < 1) {
            issueError("payroll.employee.monthlypayment.salary.mustbe.positive.integer", context)
        } else {
            this.salary = aSalary
        }
    }

    @Override
    void addPaymentAttachment(PaymentAttachment paymentAttachment) {
        throw new UnsupportedOperationException("Monthly payment does not have payment attachments")
    }
}
