package com.vmc.validationNotification.objectCreation

import com.vmc.validationNotification.ApplicationValidationNotifier
import com.vmc.validationNotification.SimpleValidationObserverImp
//TODO tests
//TODO docs
/**
 */
class ConstructorValidator extends SimpleValidationObserverImp {

    ConstructorValidator() {
        ApplicationValidationNotifier.addObserver(this)
    }

    def validateConstruction(){
        if(!successful()) throw new ConstructionValidationFailedException(getCommaSeparatedErrors())
    }

}
