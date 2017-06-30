package com.vmc.validationNotification.testPreparation

import com.vmc.validationNotification.ApplicationValidationNotifier
import com.vmc.validationNotification.SimpleValidationObserverImp
import org.junit.Before

abstract class ValidationNotificationTestSetup {

    protected SimpleValidationObserverImp validationObserver = new SimpleValidationObserverImp()

    SimpleValidationObserverImp getValidationObserver(){
        return validationObserver
    }

    @Before
    void setUp(){
        ApplicationValidationNotifier.createCurrentListOfListeners()
        validationObserver = new SimpleValidationObserverImp()
        ApplicationValidationNotifier.addObserver(validationObserver)
    }

}