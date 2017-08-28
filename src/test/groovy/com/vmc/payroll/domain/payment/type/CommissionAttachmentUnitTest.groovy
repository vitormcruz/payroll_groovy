package com.vmc.payroll.domain.payment.type

import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.payment.attachment.SalesReceipt
import com.vmc.payroll.domain.payment.attachment.TimeCard
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
        assert shouldFail(IllegalArgumentException, { commission.postPaymentAttachment([] as TimeCard) }).message == "Non Sales Receipt payment attachment was provided to " +
                                                                                                                     "a commission payment type."
        assert commission.getPaymentAttachments().isEmpty()
    }
}
