package com.vmc.validationNotification

import com.google.common.collect.SetMultimap
import com.vmc.validationNotification.objectCreation.ValidationFailedException
import org.junit.Test

import static com.vmc.validationNotification.Validation.validate
import static com.vmc.validationNotification.Validation.validateNewObject
import static groovy.test.GroovyAssert.shouldFail
import static org.junit.Assert.fail

class ValidationTest {

    @Test
    void "Validate a valid execution"(){
        def stringExpected = "Ok"
        def stringObtained
        validate {stringObtained = "Ok"}
        assert stringExpected == stringObtained
    }

    @Test
    void "Validate an invalid execution"(){
        def ex = shouldFail(ValidationFailedException, {validate {issueError("error")}})
        assert ex.message == "error"
    }

    @Test
    void "Validate an invalid execution with multiple errors"(){
        def ex = shouldFail(ValidationFailedException, {validate {
            issueError("error1")
            issueError("error2")
            issueError("error3")
            issueError("error4")
        }})

        assert ex.message == "error1, error2, error3, error4"
    }

    @Test
    void "Validate primitives objects"(){
        def ex = shouldFail(IllegalArgumentException, {validateNewObject(String, {})})
        assert ex.message == "Cannot validate primitive or arrays, also cannot validate final types since I unable to subclass them and provide an automatic generated NullObject of it " +
                             "for you."
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
