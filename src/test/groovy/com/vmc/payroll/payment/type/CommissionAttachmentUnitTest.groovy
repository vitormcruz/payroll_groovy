package com.vmc.payroll.payment.type

import com.vmc.payroll.Employee
import com.vmc.payroll.payment.paymentAttachment.SalesReceipt
import com.vmc.payroll.payment.paymentAttachment.TimeCard
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail

class CommissionAttachmentUnitTest extends ValidationNotificationTestSetup{

    @Test
    void "Add a sales recipt to a Commission payment type"(){
        def expectedTimeCard = [] as SalesReceipt
        def commission = Commission.newPaymentType([] as Employee, 1, 1)
        commission.postPaymentAttachment(expectedTimeCard)
        assert commission.getPaymentAttachments().contains(expectedTimeCard)
    }

    @Test
    void "Add another payment attachment to a Commission payment type"(){
        def commission = Commission.newPaymentType([] as Employee, 1, 1)
        def error = shouldFail(IllegalArgumentException, { commission.postPaymentAttachment([] as TimeCard) })
        assert commission.getPaymentAttachments().isEmpty()
        assert error.message == "Non Sales Receipt payment attachment was provided to a commission payment type."
    }
}
