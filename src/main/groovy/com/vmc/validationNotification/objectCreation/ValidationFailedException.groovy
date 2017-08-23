package com.vmc.validationNotification.objectCreation

class ValidationFailedException extends RuntimeException{

    ValidationFailedException(String message) {
        super(message)
    }
}
