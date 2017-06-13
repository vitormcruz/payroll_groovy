package com.vmc.payroll.payment.delivery

import com.vmc.payroll.Employee
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import groovy.test.GroovyAssert
import org.junit.Test


class AccountTransferDeliveryTest extends ValidationNotificationTestSetup{

    @Test
    void "Employee is mandatory"(){
        def ex = GroovyAssert.shouldFail(IllegalArgumentException, {AccountTransferDelivery.newPaymentDelivery(null, "bank 1", "11111")})
        assert ex.message == "Did you miss passing my employee?"
    }

    @Test
    void "Bank is mandatory"(){
        def accountTransferDelivery = AccountTransferDelivery.newPaymentDelivery([] as Employee, null, "111111")
        assert accountTransferDelivery == null
        assert validationObserver.errors.contains("payroll.account.transfer.delivery.bank.mandatory")
    }

    @Test
    void "Account is mandatory"(){
        def accountTransferDelivery = AccountTransferDelivery.newPaymentDelivery([] as Employee, "bank 1", null)
        assert accountTransferDelivery == null
        assert validationObserver.errors.contains("payroll.account.transfer.delivery.account.mandatory")
    }

    @Test
    void "Change bank to null"(){
        def accountTransferDelivery = AccountTransferDelivery.newPaymentDelivery([] as Employee, "bank 1", "111111")
        accountTransferDelivery.setBank(null)
        assert accountTransferDelivery.bank == "bank 1"
        assert validationObserver.errors.contains("payroll.account.transfer.delivery.bank.mandatory")
    }

    @Test
    void "Change account to null"(){
        def accountTransferDelivery = AccountTransferDelivery.newPaymentDelivery([] as Employee, "bank 1", "111111")
        accountTransferDelivery.setAccount(null)
        assert accountTransferDelivery.account == "111111"
        assert validationObserver.errors.contains("payroll.account.transfer.delivery.account.mandatory")
    }

    @Test
    void "Change bank to a valid value"(){
        def accountTransferDelivery = AccountTransferDelivery.newPaymentDelivery([] as Employee, "bank 1", "111111")
        accountTransferDelivery.setBank("bank 2")
        assert accountTransferDelivery.bank == "bank 2"
        assert validationObserver.successful() : "${validationObserver.getCommaSeparatedErrors()}"
    }

    @Test
    void "Change account to a valid value"(){
        def accountTransferDelivery = AccountTransferDelivery.newPaymentDelivery([] as Employee, "bank 1", "111111")
        accountTransferDelivery.setAccount("222222")
        assert accountTransferDelivery.account == "222222"
        assert validationObserver.successful() : "${validationObserver.getCommaSeparatedErrors()}"
    }


}
