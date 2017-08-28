package com.vmc.payroll.domain.payment.delivery

import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail

class PaymasterUnitTest {

    @Test
    void "Employee is mandatory"(){
        assert shouldFail(IllegalArgumentException, {Paymaster.newPaymentDelivery(null)}).message == "Did you miss passing my employee?"
    }

}
