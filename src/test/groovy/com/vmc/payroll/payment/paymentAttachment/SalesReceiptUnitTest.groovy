package com.vmc.payroll.payment.paymentAttachment

import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.joda.time.DateTime
import org.junit.Test

class SalesReceiptUnitTest extends ValidationNotificationTestSetup {

    @Test
    void "Create a sales receipt providing null to required fields"(){
        def salesReceiptBuilder = SalesReceipt.newSalesReceipt(null, null)
        assert validationObserver.errors.containsAll("payroll.salesreceipt.amount.required",
                                                     "payroll.salesreceipt.date.required")
    }

    @Test
    void "Create a sales receipt providing valid values to required fields"(){
        def expectedDateTime = new DateTime()
        def selesReceipt = SalesReceipt.newSalesReceipt(expectedDateTime, 10)
        assert selesReceipt.date == expectedDateTime
        assert selesReceipt.amount == 10
    }

}
