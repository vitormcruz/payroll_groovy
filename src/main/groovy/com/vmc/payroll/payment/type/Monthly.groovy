package com.vmc.payroll.payment.type

import com.vmc.payroll.payment.paymentAttachment.api.WorkDoneProof
import com.vmc.payroll.payment.type.api.GenericPaymentType

import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation
import static com.vmc.validationNotification.Validate.validate

class Monthly extends GenericPaymentType {

    Integer salary

    static Monthly newPaymentType(employee, Integer salary){
        return validate {new Monthly(employee, salary)}
    }

    /**
     * Should be used for reflection magic only
     */
    Monthly() {}

    /**
     * Use newPaymentType instead, otherwise be careful as you can end up with an invalid object.
     */
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
    void addPaymentAttachment(WorkDoneProof paymentAttachment) {
        throw new UnsupportedOperationException("Monthly payment does not have payment attachments")
    }
}
