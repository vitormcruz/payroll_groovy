package sandbox.magritte.validationGenerator.validations
import org.junit.Test
import sandbox.validatorJunit.ResultInterface
import sandbox.validatorJunit.imp.ValidationException

import static groovy.test.GroovyAssert.shouldFail
import static org.hamcrest.CoreMatchers.hasItem
import static org.junit.Assert.assertThat
/**
 */
abstract class BasicValidationMethodTest{

    @Test
    def void "accessor is required"(){
        ValidationException ex = shouldFail(ValidationException, {getValidationMethodWith(null)})
        assertThat(extractErrorMessagesFromResult(ex.result),
                   hasItem("sandbox.magritte.validationgenerator.methodgenerator.imp.BasicValidationMethod.createValidationMethod.validation.accessor.mandatory.error"))
    }

    protected List<String> extractErrorMessagesFromResult(ResultInterface result) {
        result.getFailures().collect { it.getException().getMessage() }
    }

    abstract def getValidationMethodWith(InstanceAccessor accessor)
}
