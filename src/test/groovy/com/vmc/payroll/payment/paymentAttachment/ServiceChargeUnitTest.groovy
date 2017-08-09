package com.vmc.payroll.payment.paymentAttachment

import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.joda.time.DateTime
import org.junit.Test

import static junit.framework.TestCase.fail

class ServiceChargeUnitTest extends ValidationNotificationTestSetup {

    @Test
    void "Create a service charge providing null to required fields"(){
        ServiceCharge serviceCharge = ServiceCharge.newServiceCharge(null, null)
        serviceCharge.onBuildSuccess({fail("Creating a Service Charge without required fields should fail.")})
                     .onBuildFailure({assert validationObserver.errors.containsAll("payroll.servicecharge.date.required", "payroll.servicecharge.amount.required")})

    }

    @Test
    void "Create a time card providing valid values to required fields"(){
        def expectedDateTime = new DateTime()
        def serviceCharge = ServiceCharge.newServiceCharge(expectedDateTime, 10)
        assert serviceCharge.date == expectedDateTime
        assert serviceCharge.amount == 10
    }


}
