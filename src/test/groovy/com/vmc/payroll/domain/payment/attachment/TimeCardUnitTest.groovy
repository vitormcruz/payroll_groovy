package com.vmc.payroll.domain.payment.attachment

import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.jupiter.api.Test

import java.time.LocalDateTime

import static org.junit.jupiter.api.Assertions.fail

class TimeCardUnitTest extends ValidationNotificationTestSetup {

    @Test
    void "Create a time card providing null to required fields"(){
        def timeCard = TimeCard.newTimeCard(null, null)
        timeCard.onBuildSuccess({fail("Creating a Time Card without required fields should fail.")})
                .onBuildFailure({assert validationObserver.errors.containsAll("payroll.timecard.date.required",
                                                                              "payroll.timecard.hours.required")})
    }

    @Test
    void "Create a time card providing valid values to required fields"(){
        def expectedDateTime = LocalDateTime.now()
        def timeCard = TimeCard.newTimeCard(expectedDateTime, 10)
        assert timeCard.date == expectedDateTime
        assert timeCard.hours == 10
    }
}
