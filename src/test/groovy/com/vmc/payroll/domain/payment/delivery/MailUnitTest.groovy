package com.vmc.payroll.domain.payment.delivery

import com.vmc.payroll.domain.Employee
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.jupiter.api.Test

import static groovy.test.GroovyAssert.shouldFail

class MailUnitTest extends ValidationNotificationTestSetup{

    @Test
    void "Employee is mandatory"(){
        assert shouldFail(IllegalArgumentException, {Mail.newPaymentDelivery(null, "Street 1")}).message == "Did you miss passing my employee?"
    }

    @Test
    void "Address is mandatory"(){
        def mailDelivery = Mail.newPaymentDelivery([] as Employee, null)
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
