package com.vmc.validationNotification.objectCreation

import com.vmc.validationNotification.ApplicationValidationNotifier
import com.vmc.validationNotification.SimpleValidationObserver
//TODO tests
//TODO docs
/**
 */
class ConstructorValidator extends SimpleValidationObserver {

    ConstructorValidator() {
        ApplicationValidationNotifier.addObserver(this)
    }

    def validateConstruction(){
        if(!successful()) throw new ConstructionValidationFailedException(getCommaSeparatedErrors())
    }

}
