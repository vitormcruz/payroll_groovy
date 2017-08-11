package com.vmc.validationNotification

import com.vmc.validationNotification.Mandatory
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import groovy.test.GroovyAssert
import org.junit.Test

class MandatoryTest extends ValidationNotificationTestSetup{

    @Test
    void "Get mandatory value when it's null"(){
        def error = GroovyAssert.shouldFail(IllegalStateException, {new Mandatory(null, "error message", [:]).get()})
        assert error.message == "error message"
    }

    @Test
    void "Verify default error message"(){
        def error = GroovyAssert.shouldFail(IllegalStateException, {new Mandatory().get()})
        assert error.message == "Mandatory value was not provided"
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
