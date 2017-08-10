package com.vmc.validationNotification.api

import com.vmc.validationNotification.ApplicationValidationNotifier
import com.vmc.validationNotification.SimpleValidationObserverImp
import com.vmc.validationNotification.builder.ConstructionValidationFailedException


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
