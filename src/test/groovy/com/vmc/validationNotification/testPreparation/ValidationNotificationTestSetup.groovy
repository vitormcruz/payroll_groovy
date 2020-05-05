package com.vmc.validationNotification.testPreparation

import com.vmc.validationNotification.ApplicationValidationNotifier
import com.vmc.validationNotification.SimpleValidationObserver
import org.junit.jupiter.api.BeforeEach

abstract class ValidationNotificationTestSetup {

    protected SimpleValidationObserver validationObserver = new SimpleValidationObserver()

    SimpleValidationObserver getValidationObserver(){
        return validationObserver
    }

    @BeforeEach
    void setUp(){
        ApplicationValidationNotifier.createCurrentListOfListeners()
        validationObserver = new SimpleValidationObserver()
        ApplicationValidationNotifier.addObserver(validationObserver)
    }

}