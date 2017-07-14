package com.vmc.validationNotification.api

import com.google.common.collect.SetMultimap

interface SimpleValidationObserver extends ValidationObserver{

    def getErrors()
    SetMultimap getErrorsByContext()

}