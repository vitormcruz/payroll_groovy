package com.vmc.payroll.payment.delivery

import com.vmc.payroll.Employee
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import groovy.test.GroovyAssert
import org.junit.Test

class MailTest extends ValidationNotificationTestSetup{

    @Test
    void "Employee is mandatory"(){
        def ex = GroovyAssert.shouldFail(IllegalArgumentException, {Mail.newPaymentDelivery(null, "Street 1")})
        assert ex.message == "Did you miss passing my employee?"
    }

    @Test
    void "Address is mandatory"(){
        def mailDelivery = Mail.newPaymentDelivery([] as Employee, null)
        assert mailDelivery == null
        assert validationObserver.errors.contains("The address for mail delivery is required")
    }

    @Test
    void "Creating valid Mail Delivery"(){
        def mailDelivery = Mail.newPaymentDelivery([] as Employee, "Street 1")
        assert mailDelivery.address == "Street 1"
        assert validationObserver.successful() : "${validationObserver.getCommaSeparatedErrors()}"
    }

    @Test
    void "Changing address to null"(){
        def mailDelivery = Mail.newPaymentDelivery([] as Employee, "Street 1")
        mailDelivery.setAddress(null)
        assert mailDelivery.address == "Street 1"
        assert validationObserver.errors.contains("The address for mail delivery is required")
    }

    @Test
    void "Changing address"(){
        def mailDelivery = Mail.newPaymentDelivery([] as Employee, "Street 1")
        mailDelivery.setAddress("Street 2")
        assert mailDelivery.address == "Street 2"
        assert validationObserver.successful() : "${validationObserver.getCommaSeparatedErrors()}"
    }

}
