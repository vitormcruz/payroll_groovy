package com.vmc.validationNotification

import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.Test

import static junit.framework.TestCase.fail

class RequiredValidationUnitTest extends ValidationNotificationTestSetup{

    @Test
    void "Creating a mandatory validation issues a new mandatory obligation"(){
        new RequiredValidation(this, [:], "teste", "expectedError")
        assert validationObserver.errors.contains("expectedError")
    }

    @Test
    void "Setting null spam error"(){
        RequiredValidation mandatoryValidation = new RequiredValidation(this, [:], "teste", "expectedError")
        mandatoryValidation.set(null, {fail("Setting null using a mandatory validation should not issue the success closure")},
                                      {})

        assert validationObserver.errors.contains("expectedError")
    }

    @Test
    void "Setting non null value should be successfull"(){
        RequiredValidation mandatoryValidation = new RequiredValidation(this, [:], "teste", "expectedError")
        mandatoryValidation.set("test", {},
                                        {fail("Setting null using a mandatory validation should not issue the fail closure")})
        assert validationObserver.errors.isEmpty()
    }
}
