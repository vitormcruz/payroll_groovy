package com.vmc.payroll.payment.type

import com.vmc.payroll.Employee
import com.vmc.payroll.payment.attachment.PaymentAttachment
import com.vmc.validationNotification.builder.BuilderAwareness
import com.vmc.validationNotification.builder.imp.GenericBuilder

import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation
import static com.vmc.validationNotification.ApplicationValidationNotifier.issueError

class Monthly extends GenericPaymentType implements BuilderAwareness{

    Integer salary

    public static Monthly newPaymentType(Employee employee, Integer salary){
        return new GenericBuilder(Monthly).withEmployee(employee).withSalary(salary).build()
    }

    //Should be used by builder only
    protected Monthly() {
        super()
        //Available only for reflection magic
        invalidForBuilder()
    }

    protected Monthly(Employee employee, Integer aSalary) {
        super(employee)
        executeNamedValidation("Validate new Monhtly Payment", {
            setSalary(aSalary)
        })
    }

    void setSalary(Integer aSalary) {
        def context = [name: "salary"]
        if (aSalary == null) {
            issueError(this, context, "payroll.employee.monthlypayment.salary.mandatory")
        } else if (aSalary < 1) {
            issueError(this, context, "payroll.employee.monthlypayment.salary.mustbe.positive.integer")
        } else {
            this.salary = aSalary
        }
    }

    @Override
    void addPaymentAttachment(PaymentAttachment paymentAttachment) {
        throw new UnsupportedOperationException("Monthly payment does not have payment attachments")
    }
}
