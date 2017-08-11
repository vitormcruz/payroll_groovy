package com.vmc.payroll.domain.payment.delivery

import groovy.test.GroovyAssert
import org.junit.Test


class PaymasterTest {

    @Test
    void "Employee is mandatory"(){
        def ex = GroovyAssert.shouldFail(IllegalArgumentException, {Paymaster.newPaymentDelivery(null)})
        assert ex.message == "Did you miss passing my employee?"
    }

}
