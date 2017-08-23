package com.vmc.payroll.domain.payment.type

import com.vmc.payroll.domain.payment.attachment.api.WorkDoneProof

import static com.vmc.validationNotification.Validate.validate
import static com.vmc.validationNotification.Validate.validateNewObject

class Monthly extends GenericPaymentType {

    Integer salary

    static Monthly newPaymentType(employee, Integer salary){
        return validateNewObject(Monthly, {new Monthly(employee, salary)})
    }

    /**
     * Should be used for reflection magic only
     */
    Monthly() {
    }

    Monthly(employee, Integer aSalary) {
        validate {initialize(employee, aSalary)}
    }

    void initialize(Object anEmployee, Integer aSalary) {
        super.initialize(anEmployee)
        setSalary(aSalary)
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
    void addPaymentAttachment(WorkDoneProof paymentAttachment) {
        throw new UnsupportedOperationException("Monthly payment does not have payment attachments")
    }
}
