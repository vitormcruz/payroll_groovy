package com.vmc.payroll.payment.delivery

import com.vmc.payroll.Employee
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import groovy.test.GroovyAssert
import org.junit.Test

class MailDeliveryTest extends ValidationNotificationTestSetup{

    @Test
    def void "Employee is mandatory"(){
        def ex = GroovyAssert.shouldFail(IllegalArgumentException, {MailDelivery.newPaymentDelivery(null, "Street 1")})
        assert ex.message == "Did you miss passing my employee?"
    }

    @Test
    def void "Address is mandatory"(){
        def mailDelivery = MailDelivery.newPaymentDelivery([] as Employee, null)
        assert mailDelivery == null
        assert validationObserver.errors.contains("payroll.mail.delivery.address.mandatory")
    }

    @Test
    def void "Creating valid Mail Delivery"(){
        def mailDelivery = MailDelivery.newPaymentDelivery([] as Employee, "Street 1")
        assert mailDelivery.address == "Street 1"
        assert validationObserver.successful() : "${validationObserver.getCommaSeparatedErrors()}"
    }

    @Test
    def void "Changing address to null"(){
        def mailDelivery = MailDelivery.newPaymentDelivery([] as Employee, "Street 1")
        mailDelivery.setAddress(null)
        assert mailDelivery.address == "Street 1"
        assert validationObserver.errors.contains("payroll.mail.delivery.address.mandatory")
    }

    @Test
    def void "Changing address"(){
        def mailDelivery = MailDelivery.newPaymentDelivery([] as Employee, "Street 1")
        mailDelivery.setAddress("Street 2")
        assert mailDelivery.address == "Street 2"
        assert validationObserver.successful() : "${validationObserver.getCommaSeparatedErrors()}"
    }

}
