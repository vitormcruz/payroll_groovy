package com.vmc.validationNotification.objectCreation

/**
 * Throw me when an object construction fails. The problem on using me is that anyone trying to
 */
class ConstructionValidationFailedException extends RuntimeException{

    ConstructionValidationFailedException(String message) {
        super(message)
    }
}
