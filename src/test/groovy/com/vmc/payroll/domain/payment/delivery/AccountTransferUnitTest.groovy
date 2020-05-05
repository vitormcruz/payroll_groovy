package com.vmc.payroll.domain.payment.delivery

import com.vmc.payroll.domain.Employee
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.jupiter.api.Test

import static groovy.test.GroovyAssert.shouldFail

class AccountTransferUnitTest extends ValidationNotificationTestSetup{

    @Test
    void "Employee is mandatory"(){
        assert shouldFail(IllegalArgumentException, {AccountTransfer.newPaymentDelivery(null, "bank 1", "11111")}).message == "Did you miss passing my employee?"
    }

    @Test
    void "Bank is mandatory"(){
        def accountTransferDelivery = AccountTransfer.newPaymentDelivery([] as Employee, null, "111111")
        accountTransferDelivery.toString()
        assert validationObserver.errors.contains("The bank is required")
    }

    @Test
    void "Account is mandatory"(){
        def accountTransferDelivery = AccountTransfer.newPaymentDelivery([] as Employee, "bank 1", null)
        assert validationObserver.errors.contains("The account is required")
    }

    @Test
    void "Change bank to null"(){
        def accountTransferDelivery = AccountTransfer.newPaymentDelivery([] as Employee, "bank 1", "111111")
        accountTransferDelivery.setBank(null)
        assert validationObserver.errors.contains("The bank is required")
    }

    @Test
    void "Change account to null"(){
        def accountTransferDelivery = AccountTransfer.newPaymentDelivery([] as Employee, "bank 1", "111111")
        accountTransferDelivery.setAccount(null)
        assert accountTransferDelivery.account == "111111"
        assert validationObserver.errors.contains("The account is required")
    }

    @Test
    void "Change bank to a valid value"(){
        def accountTransferDelivery = AccountTransfer.newPaymentDelivery([] as Employee, "bank 1", "111111")
        accountTransferDelivery.setBank("bank 2")
        assert accountTransferDelivery.bank == "bank 2"
        assert validationObserver.successful() : "${validationObserver.getCommaSeparatedErrors()}"
    }

    @Test
    void "Change account to a valid value"(){
        def accountTransferDelivery = AccountTransfer.newPaymentDelivery([] as Employee, "bank 1", "111111")
        accountTransferDelivery.setAccount("222222")
        assert accountTransferDelivery.account == "222222"
        assert validationObserver.successful() : "${validationObserver.getCommaSeparatedErrors()}"
    }


}
