package com.vmc.validationNotification.builder


class UsedForbiddenConstructor extends RuntimeException{

    UsedForbiddenConstructor(String message) {
        super(message)
    }

}
