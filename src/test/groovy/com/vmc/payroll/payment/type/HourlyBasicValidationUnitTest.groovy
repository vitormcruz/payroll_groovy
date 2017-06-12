package com.vmc.payroll.payment.type

import com.vmc.payroll.Employee
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.Test

class HourlyBasicValidationUnitTest extends ValidationNotificationTestSetup{


    public static final int VALID_HOUR_RATE = 3

    @Test
    def void "Validate positive Hour Rate"(){
        def hourRate = Hourly.newPaymentType([] as Employee, VALID_HOUR_RATE)
        assert hourRate != null
        assert hourRate.getHourRate() == VALID_HOUR_RATE
    }

    @Test
    def void "Validate negative Hour Rate"(){
        assert getHourlyWith(-1) == null
        assert validationObserver.errors.contains("payroll.employee.hourlypayment.hourRate.mustbe.positive.integer")
    }

    @Test
    def void "Validate zero Hour Rate"(){
        assert getHourlyWith(0) == null
        assert validationObserver.errors.contains("payroll.employee.hourlypayment.hourRate.mustbe.positive.integer")
    }

    @Test
    def void "Provide null to the Hour Rate"(){
        assert getHourlyWith(null) == null
        assert validationObserver.getErrors().contains("payroll.employee.hourlypayment.hourRate.mandatory")
    }

    @Test
    def void "Change to a positive Hour Rate"(){
        def hourRate = getValidHourly()
        hourRate.setHourRate(1)
        assert hourRate.getHourRate() == 1
        assert validationObserver.successful() : "${validationObserver.getCommaSeparatedErrors()}"
    }

    @Test
    def void "Change to a negative Hour Rate"(){
        def hourRate = getValidHourly()
        hourRate.setHourRate(-1)
        assert hourRate.getHourRate() == VALID_HOUR_RATE
        assert validationObserver.errors.contains("payroll.employee.hourlypayment.hourRate.mustbe.positive.integer")
    }

    @Test
    def void "Change to a zero Hour Rate"(){
        def hourRate = getValidHourly()
        hourRate.setHourRate(0)
        assert hourRate.getHourRate() == VALID_HOUR_RATE
        assert validationObserver.errors.contains("payroll.employee.hourlypayment.hourRate.mustbe.positive.integer")
    }

    @Test
    def void "Change to a null to the Hour Rate"(){
        def hourRate = getValidHourly()
        hourRate.setHourRate(null)
        assert hourRate.getHourRate() == VALID_HOUR_RATE
        assert validationObserver.getErrors().contains("payroll.employee.hourlypayment.hourRate.mandatory")
    }

    public Hourly getValidHourly() {
        getHourlyWith(VALID_HOUR_RATE)
    }

    public Hourly getHourlyWith(Integer hourRate) {
        Hourly.newPaymentType([] as Employee, hourRate)
    }
}
