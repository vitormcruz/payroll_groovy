package com.vmc.payroll.adapter.json

import com.cedarsoftware.util.io.JsonWriter
import com.vmc.payroll.domain.Employee

import static com.cedarsoftware.util.io.JsonWriter.FIELD_NAME_BLACK_LIST

class JsonConversionExtensions {

    private static final writerOptions = [(FIELD_NAME_BLACK_LIST): [(Employee): ["paymentAttachmentListeners"]]
                                         ]

    static String toJson(Object object){
        return JsonWriter.objectToJson(object, writerOptions)
    }
}
