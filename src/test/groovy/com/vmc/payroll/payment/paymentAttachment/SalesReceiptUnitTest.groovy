package com.vmc.payroll.payment.paymentAttachment

import org.joda.time.DateTime
import org.junit.Test
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import com.vmc.validationNotification.builder.GenericBuilder

import static junit.framework.TestCase.fail

class SalesReceiptUnitTest extends ValidationNotificationTestSetup {

    @Test
    void "Create a sales receipt providing null to required fields"(){
        def salesReceiptBuilder = new GenericBuilder(SalesReceipt).withDate(null)
                                                                  .withAmount(null)
        salesReceiptBuilder.buildAndDo(
          {fail("Creating a Sales Receipt without required fields should fail.")},
          {assert validationObserver.errors.containsAll("payroll.salesreceipt.amount.required",
                                                        "payroll.salesreceipt.date.required")})
    }

    @Test
    void "Create a sales receipt providing valid values to required fields"(){
        def selesReceipt = new GenericBuilder(SalesReceipt)
        def expectedDateTime = new DateTime()
        selesReceipt.withConstructorArgs(expectedDateTime, 10)
        selesReceipt.buildAndDo(
          {assert it.date == expectedDateTime
           assert it.amount == 10 },
          {fail("Creating a Sales Receipt with required fields should be successful.")})

    }

}
