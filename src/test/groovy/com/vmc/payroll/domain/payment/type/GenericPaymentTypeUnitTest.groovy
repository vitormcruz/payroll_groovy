package com.vmc.payroll.domain.payment.type

import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.payment.attachment.api.UnionCharge
import com.vmc.payroll.domain.payment.attachment.api.WorkDoneProof
import org.junit.jupiter.api.Test

import static groovy.test.GroovyAssert.shouldFail

class GenericPaymentTypeUnitTest {

    @Test
    void "Provide null for employee"(){
        assert shouldFail({new GenericPaymentTypeForTest(null)}).message == "Employee must be provided for payment types, but I got it null"
    }

    @Test
    void "Provide a valid employee"(){
        def expectedEmployee = [] as Employee
        assert new GenericPaymentTypeForTest(expectedEmployee).employee == expectedEmployee
    }

    @Test
    void "Adding a payment attachment"(){
        def paymentType = new GenericPaymentTypeForTest([] as Employee)
        def paymentAttachmentExpected = [] as WorkDoneProof
        paymentType.postPaymentAttachment(paymentAttachmentExpected)
        assert paymentType.getPaymentAttachments().contains(paymentAttachmentExpected)
    }

    @Test
    void "Adding a non payment attachment"(){
        def paymentType = new GenericPaymentTypeForTest([] as Employee)
        def nonPaymentAttachment = [] as UnionCharge
        paymentType.postPaymentAttachment(nonPaymentAttachment)
        assert !paymentType.getPaymentAttachments().contains(nonPaymentAttachment)
    }

    class GenericPaymentTypeForTest extends GenericPaymentType{

        GenericPaymentTypeForTest(anEmployee) {
            super(anEmployee)
        }
    }
}
