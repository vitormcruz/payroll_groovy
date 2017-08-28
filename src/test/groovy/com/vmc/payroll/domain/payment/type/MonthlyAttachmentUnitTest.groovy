package com.vmc.payroll.domain.payment.type

import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.payment.attachment.api.WorkDoneProof
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail

class MonthlyAttachmentUnitTest extends ValidationNotificationTestSetup{

    @Test
    void "Add a payment attachment to a Monthly payment type"(){
        assert shouldFail({Monthly.newPaymentType([] as Employee,1).postPaymentAttachment({} as WorkDoneProof)}).message == "Monthly payment does not have payment attachments"
    }

}
