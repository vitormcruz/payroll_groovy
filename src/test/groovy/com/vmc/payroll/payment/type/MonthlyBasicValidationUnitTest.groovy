package com.vmc.payroll.payment.type

import com.vmc.payroll.Employee
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.Test

class MonthlyBasicValidationUnitTest extends ValidationNotificationTestSetup{


    public static final int VALID_SALARY = 10

    @Test
    def void "Validate positive Salary"(){
        def monthlySalary = getMonthlyPaymentTypeWith(VALID_SALARY)
        assert monthlySalary != null
        assert monthlySalary.getSalary() == VALID_SALARY
        assert validationObserver.successful() : "${validationObserver.getCommaSeparatedErrors()}"
    }

    @Test
    def void "Validate negative Salary"(){
        assert getMonthlyPaymentTypeWith(-1) == null
        assert validationObserver.errors.contains("payroll.employee.monthlypayment.salary.mustbe.positive.integer")
    }

    @Test
    def void "Validate zero Salary"(){
        assert getMonthlyPaymentTypeWith(0) == null
        assert validationObserver.errors.contains("payroll.employee.monthlypayment.salary.mustbe.positive.integer")
    }

    @Test
    def void "Provide null to the salary"(){
        assert getMonthlyPaymentTypeWith(null) == null
        assert validationObserver.getErrors().contains("payroll.employee.monthlypayment.salary.mandatory")
    }

    @Test
    def void "Change to a positive Salary"(){
        def monthlySalary = getValidaMonthlyPaymentType()
        monthlySalary.setSalary(100)
        assert monthlySalary.getSalary() == 100
        assert validationObserver.successful() : "${validationObserver.getCommaSeparatedErrors()}"
    }

    @Test
    def void "Change to a negative Salary"(){
        def monthlySalary = getValidaMonthlyPaymentType()
        monthlySalary.setSalary(-1)
        assert monthlySalary.getSalary() == VALID_SALARY
        assert validationObserver.errors.contains("payroll.employee.monthlypayment.salary.mustbe.positive.integer")
    }

    @Test
    def void "Change to a zero Salary"(){
        def monthlySalary = getValidaMonthlyPaymentType()
        monthlySalary.setSalary(0)
        assert monthlySalary.getSalary() == VALID_SALARY
        assert validationObserver.errors.contains("payroll.employee.monthlypayment.salary.mustbe.positive.integer")
    }

    @Test
    def void "Change to a null to the salary"(){
        def monthlySalary = getValidaMonthlyPaymentType()
        monthlySalary.setSalary(null)
        assert monthlySalary.getSalary() == VALID_SALARY
        assert validationObserver.getErrors().contains("payroll.employee.monthlypayment.salary.mandatory")
    }

    public Monthly getValidaMonthlyPaymentType() {
        return getMonthlyPaymentTypeWith(VALID_SALARY)
    }

    public Monthly getMonthlyPaymentTypeWith(Integer salary) {
        return Monthly.newPaymentType([] as Employee, salary)
    }
}
