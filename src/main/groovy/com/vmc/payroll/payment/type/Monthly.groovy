package com.vmc.payroll.payment.type

import com.vmc.payroll.payment.paymentAttachment.api.WorkDoneProof
import com.vmc.validationNotification.objectCreation.ConstructorValidator

import static com.vmc.validationNotification.Validate.validate

class Monthly extends GenericPaymentType {

    Integer salary

    static Monthly newPaymentType(employee, Integer salary){
        return validate(Monthly, {new Monthly(employee, salary)})
    }

    /**
     * Should be used for reflection magic only
     */
    Monthly() {
    }

    Monthly(employee, Integer aSalary) {
        def constructorValidator = new ConstructorValidator()
        prepareConstructor(employee, aSalary)
        constructorValidator.validateConstruction()
    }

    void prepareConstructor(Object anEmployee, Integer aSalary) {
        super.prepareConstructor(anEmployee)
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
