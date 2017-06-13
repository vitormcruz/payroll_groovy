package com.vmc.payroll.payment.delivery

import groovy.test.GroovyAssert
import org.junit.Test


class PaymasterDeliveryTest {

    @Test
    void "Employee is mandatory"(){
        def ex = GroovyAssert.shouldFail(IllegalArgumentException, {PaymasterDelivery.newPaymentDelivery(null)})
        assert ex.message == "Did you miss passing my employee?"
    }

}
