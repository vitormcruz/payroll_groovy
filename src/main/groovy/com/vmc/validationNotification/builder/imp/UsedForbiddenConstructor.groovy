package com.vmc.validationNotification.builder.imp


class UsedForbiddenConstructor extends RuntimeException{

    UsedForbiddenConstructor(String message) {
        super(message)
    }

}
