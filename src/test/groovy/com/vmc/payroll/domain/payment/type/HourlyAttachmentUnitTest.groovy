package com.vmc.payroll.domain.payment.type

import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.payment.attachment.SalesReceipt
import com.vmc.payroll.domain.payment.attachment.TimeCard
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.jupiter.api.Test

import static groovy.test.GroovyAssert.shouldFail

class HourlyAttachmentUnitTest extends ValidationNotificationTestSetup{

    @Test
    void "Add a time card to a Hourly payment type"(){
        def expectedTimeCard = [] as TimeCard
        def hourly = Hourly.newPaymentType([] as Employee, 1)
        hourly.postPaymentAttachment(expectedTimeCard)
        assert hourly.getPaymentAttachments().contains(expectedTimeCard)
    }

    @Test
    void "Add another payment attachment to a Hourly payment type"(){
        def hourly =  Hourly.newPaymentType([] as Employee, 1)
        assert shouldFail(IllegalArgumentException, { hourly.postPaymentAttachment([] as SalesReceipt) }).message == "Non Time Card payment attachment was provided to a " +
                                                                                                                     "hourly payment type."
        assert hourly.getPaymentAttachments().isEmpty()
    }

}
