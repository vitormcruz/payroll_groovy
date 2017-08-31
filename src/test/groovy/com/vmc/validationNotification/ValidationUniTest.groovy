package com.vmc.validationNotification

import com.google.common.collect.SetMultimap
import org.junit.Test

import static com.vmc.validationNotification.Validation.validate
import static com.vmc.validationNotification.Validation.validateNewObject
import static groovy.test.GroovyAssert.shouldFail
import static org.junit.Assert.fail

class ValidationUniTest {

    @Test
    void "Validate a valid execution"(){
        def stringExpected = "Ok"
        def stringObtained
        validate {stringObtained = "Ok"}
        assert stringExpected == stringObtained
    }

    @Test
    void "Validate an invalid execution"(){
        shouldFail(ValidationFailedException, {validate {issueError("error")}}).message == "error"
    }

    @Test
    void "Validate an invalid execution with multiple errors"(){
        assert shouldFail(ValidationFailedException, {validate {
            issueError("error1")
            issueError("error2")
            issueError("error3")
            issueError("error4")
        }}).message == "error1, error2, error3, error4"
    }

    @Test
    void "Validate primitives objects"(){
        assert shouldFail(IllegalArgumentException, {validateNewObject(String, {})}).message == "Cannot validate primitive or arrays, also cannot validate final types since I " +
                                                                                                "unable to subclass them and provide an automatic generated NullObject of it for you."
    }

    @Test
    void "Validate a valid object"(){
        def expectedObject = new SimpleValidationObserver()
        def objectObtained = validateNewObject(SimpleValidationObserver, {expectedObject})
        assert objectObtained == expectedObject
    }

    @Test
    void "Validate an invalid object"(){
        def object = new SimpleValidationObserver()
        def objectObtained = validateNewObject(SimpleValidationObserver, {
            issueError("error1")
            issueError("error2")
            return object
        })

        assert objectObtained.onBuildSuccess {fail("Object creation should have failed")}
        SetMultimap errorsObtained
        assert objectObtained.onBuildFailure { errorsObtained = it}
        assert (errorsObtained.values() as Set).unique() == ["error1", "error2"] as Set
    }

}
