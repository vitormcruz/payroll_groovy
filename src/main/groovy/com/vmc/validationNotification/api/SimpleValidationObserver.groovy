package com.vmc.validationNotification.api

import com.google.common.collect.Table


interface SimpleValidationObserver extends ValidationObserver{

    def getErrors()
    Table<Object, Object, String> getErrorsByContext()

}