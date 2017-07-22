package com.vmc.payroll.payment.paymentAttachment

import org.joda.time.DateTime
import org.junit.Test
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import com.vmc.validationNotification.builder.GenericBuilder

import static junit.framework.TestCase.fail

class TimeCardUnitTest extends ValidationNotificationTestSetup {

    @Test
    void "Create a time card providing null to required fields"(){
        def timeCardBuilder = new GenericBuilder(TimeCard).withDate(null)
                                                          .withTime(null)
        timeCardBuilder.buildAndDo(
          {fail("Creating a Time Card without required fields should fail.")},
          {assert validationObserver.errors.containsAll("payroll.timecard.date.required",
                                                        "payroll.timecard.hours.required")})

    }

    @Test
    void "Create a time card providing valid values to required fields"(){
        def timeCardBuilder = new GenericBuilder(TimeCard)
        def expectedDateTime = new DateTime()
        timeCardBuilder.with(expectedDateTime, 10)
        timeCardBuilder.buildAndDo(
          {assert it.date == expectedDateTime
           assert it.hours == 10 },
          {fail("Creating a Time Card with required fields should be successful.")})
    }
}
