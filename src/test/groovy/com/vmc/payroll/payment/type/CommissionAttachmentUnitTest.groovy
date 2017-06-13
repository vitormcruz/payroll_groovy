package com.vmc.payroll.payment.type

import com.vmc.payroll.Employee
import com.vmc.payroll.payment.attachment.SalesReceipt
import com.vmc.payroll.payment.attachment.TimeCard
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.Test

class CommissionAttachmentUnitTest extends ValidationNotificationTestSetup{

    @Test
    void "Add a sales recipt to a Commission payment type"(){
        def expectedTimeCard = [] as SalesReceipt
        def commission = Commission.newPaymentType([] as Employee, 1, 1)
        commission.postWorkEvent(expectedTimeCard)
        assert commission.getPaymentAttachments().contains(expectedTimeCard)
    }

    @Test
    void "Add another payment attachment to a Commission payment type"(){
        def commission = Commission.newPaymentType([] as Employee, 1, 1)
        commission.postWorkEvent([] as TimeCard)
        assert commission.getPaymentAttachments().isEmpty()
        validationObserver.errors.contains("employee.payment.commission.sales.receipt.payment.info.only")
    }
}