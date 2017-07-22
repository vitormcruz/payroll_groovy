package com.vmc.payroll.payment.type

import com.vmc.payroll.Employee
import org.junit.Test

class CommissionBasicValidationUnitTest extends MonthlyBasicValidationUnitTest{

    @Test
    void "Validate positive Commission Rate"(){
        def commision = getCommissionPaymentTypeWith(1, 500)
        assert commision != null
        assert commision.getCommissionRate() == 500
    }

    @Test
    void "Validate negative Commission Rate"(){
        getCommissionPaymentTypeWith(1, -1)
        assert validationObserver.errors.contains("The commission rate must be a positive integer")
    }

    @Test
    void "Validate zero Commission Rate"(){
        getCommissionPaymentTypeWith(1, 0)
        assert validationObserver.errors.contains("The commission rate must be a positive integer")
    }

    @Test
    void "Provide null to the Commission Rate"(){
        getCommissionPaymentTypeWith(1, null)
        assert validationObserver.getErrors().contains("The commission rate is required")
    }

    @Test
    void "Change to a positive Commission Rate"(){
        def commision = getValidCommissionPaymentType()
        commision.setCommissionRate(100)
        assert commision.getCommissionRate() == 100
        assert validationObserver.successful() : "${validationObserver.getCommaSeparatedErrors()}"
    }

    @Test
    void "Change to a negative Commission Rate"(){
        def commision = getValidCommissionPaymentType()
        commision.setCommissionRate(-1)
        assert commision.getCommissionRate() == 500
        assert validationObserver.errors.contains("The commission rate must be a positive integer")
    }

    @Test
    void "Change to a zero Commission Rate"(){
        def commision = getValidCommissionPaymentType()
        commision.setCommissionRate(0)
        assert commision.getCommissionRate() == 500
        assert validationObserver.errors.contains("The commission rate must be a positive integer")
    }

    @Test
    void "Change to a null to the Commission Rate"(){
        def commision = getValidCommissionPaymentType()
        commision.setCommissionRate(null)
        assert commision.getCommissionRate() == 500
        assert validationObserver.getErrors().contains("The commission rate is required")
    }

    @Override
    Monthly getMonthlyPaymentTypeWith(Integer salary) {
        return Commission.newPaymentType([] as Employee, salary, 500)
    }

    Commission getValidCommissionPaymentType() {
        return getCommissionPaymentTypeWith(1, 500)
    }

    Commission getCommissionPaymentTypeWith(Integer salary, Integer rate) {
        return Commission.newPaymentType([] as Employee, salary, rate)
    }
}
