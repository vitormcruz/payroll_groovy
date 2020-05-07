package com.vmc.validationNotification

import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.jupiter.api.Test

import static groovy.test.GroovyAssert.shouldFail

class MandatoryUnitTest extends ValidationNotificationTestSetup{

    @Test
    void "New mandatory with null error message"(){
        assert shouldFail(IllegalArgumentException,
                {new Mandatory(null, null)}).message == "Error message was not provided"
    }

    @Test
    void "Call getter with function null"(){
        assert shouldFail(IllegalArgumentException,
                {new Mandatory("test", null).get(null)}).message == "Getter function was not provided"
    }

    @Test
    void "Call setter with function null"(){
        assert shouldFail(IllegalArgumentException,
                {new Mandatory("test", null).set(null, null)}).message == "Setter function was not provided"
    }

    @Test
    void "Get mandatory value when it's null"(){
        assert shouldFail(IllegalStateException,
                {new Mandatory("error message", [:]).get({null})}).message == "error message"
    }

    @Test
    void "Get mandatory value when it's != null"(){
        assert new Mandatory("error message", [:]).get({"some value"}) == "some value"
    }

    @Test
    void "Set mandatory value with null"(){
        new Mandatory("error message", [:]).set(null, {})
        assert validationObserver.errors.contains("error message")
    }

    @Test
    void "Set mandatory value != null"(){
        new Mandatory("error message", [:]).set("some value", {})
        assert validationObserver.errors.isEmpty()
    }

    @Test
    void "Set mandatory value != null, but then setting back to null"(){
        def mandatory = new Mandatory("error message", [:])
        mandatory.set("some value", {})
        mandatory.set(null, {})
        assert validationObserver.errors.contains("error message")
    }

}
