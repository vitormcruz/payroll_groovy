package com.vmc.validationNotification

class ValidationFailedException extends RuntimeException{

    ValidationFailedException(String message) {
        super(message)
    }
}
