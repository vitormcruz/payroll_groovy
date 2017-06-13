package com.vmc.payroll.payment.type

import com.vmc.payroll.Employee
import com.vmc.payroll.payment.workEvent.SalesReceipt
import com.vmc.payroll.payment.workEvent.TimeCard
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.Test

class HourlyAttachmentUnitTest extends ValidationNotificationTestSetup{

    @Test
    void "Add a time card to a Hourly payment type"(){
        def expectedTimeCard = [] as TimeCard
        def hourly = Hourly.newPaymentType([] as Employee, 1)
        hourly.postWorkEvent(expectedTimeCard)
        assert hourly.getPaymentAttachments().contains(expectedTimeCard)
    }

    @Test
    void "Add another payment attachment to a Hourly payment type"(){
        def hourly =  Hourly.newPaymentType([] as Employee, 1)
        hourly.postWorkEvent([] as SalesReceipt)
        assert hourly.getPaymentAttachments().isEmpty()
        validationObserver.errors.contains("employee.payment.hourly.time.card.payment.info.only")
    }

}
