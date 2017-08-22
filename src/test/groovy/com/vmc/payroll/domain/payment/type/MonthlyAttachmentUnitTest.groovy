package com.vmc.payroll.domain.payment.type

import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.payment.attachment.api.WorkDoneProof
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import groovy.test.GroovyAssert
import org.junit.Test

class MonthlyAttachmentUnitTest extends ValidationNotificationTestSetup{

    @Test
    void "Add a payment attachment to a Monthly payment type"(){
        def ex = GroovyAssert.shouldFail {Monthly.newPaymentType([] as Employee,1).postPaymentAttachment({} as WorkDoneProof)}
        assert ex.message == "Monthly payment does not have payment attachments"
    }

}
