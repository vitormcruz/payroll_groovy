package com.vmc.payroll.payment.type

import com.vmc.payroll.Employee
import com.vmc.payroll.payment.attachment.api.PaymentAttachment
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import groovy.test.GroovyAssert
import org.junit.Test

class MonthlyAttachmentUnitTest extends ValidationNotificationTestSetup{

    @Test
    void "Add a payment attachment to a Monthly payment type"(){
        def ex = GroovyAssert.shouldFail {Monthly.newPaymentType([] as Employee,1).postWorkEvent({} as PaymentAttachment)}
        assert ex.message == "Monthly payment does not have payment attachments"
    }

}
