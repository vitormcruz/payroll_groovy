package com.vmc.validationNotification.testPreparation

import com.vmc.validationNotification.ApplicationValidationNotifier
import com.vmc.validationNotification.SimpleValidationObserver
import org.junit.Before

abstract class ValidationNotificationTestSetup {

    protected SimpleValidationObserver validationObserver = new SimpleValidationObserver()

    SimpleValidationObserver getValidationObserver(){
        return validationObserver
    }

    @Before
    void setUp(){
        ApplicationValidationNotifier.createCurrentListOfListeners()
        validationObserver = new SimpleValidationObserver()
        ApplicationValidationNotifier.addObserver(validationObserver)
    }

}