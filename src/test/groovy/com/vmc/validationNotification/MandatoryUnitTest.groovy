package com.vmc.validationNotification

import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail

class MandatoryUnitTest extends ValidationNotificationTestSetup{

    @Test
    void "Get mandatory value when it's null"(){
        assert shouldFail(IllegalStateException, {new Mandatory(null, "error message", [:]).get()}).message == "error message"
    }

    @Test
    void "Verify default error message"(){
        assert shouldFail(IllegalStateException, {new Mandatory().get()}).message == "Mandatory value was not provided"
    }

    @Test
    void "New mandatory without value"(){
        new Mandatory()
        assert validationObserver.errors.contains("Mandatory value was not provided")
    }

    @Test
    void "New mandatory with a null value"(){
        new Mandatory(null)
        assert validationObserver.errors.contains("Mandatory value was not provided")
    }

    @Test
    void "New mandatory with a null value and a different error message"(){
        new Mandatory(null, "Different Error Message", [:])
        assert validationObserver.errors.contains("Different Error Message")
    }

    @Test
    void "New mandatory with value"(){
        def mandatory = new Mandatory("test")
        assert validationObserver.errors.isEmpty()
        assert mandatory.get() == "test"
    }

    @Test
    void "New mandatory without value, but setting valid value after"(){
        def mandatory = new Mandatory()
        mandatory.set("test")
        assert validationObserver.errors.isEmpty()
        assert mandatory.get() == "test"
    }

    @Test
    void "New mandatory without value and setting null after"(){
        def mandatory = new Mandatory()
        mandatory.set(null)
        assert validationObserver.errors.contains("Mandatory value was not provided")
    }

    @Test
    void "New mandatory with value but setting null after"(){
        def mandatory = new Mandatory("test")
        mandatory.set(null)
        assert validationObserver.errors.contains("Mandatory value was not provided")
        assert mandatory.get() == "test"
    }

    @Test
    void "New mandatory with value and setting another value after"(){
        def mandatory = new Mandatory("test")
        mandatory.set("another test")
        assert mandatory.get() == "another test"
    }
}
