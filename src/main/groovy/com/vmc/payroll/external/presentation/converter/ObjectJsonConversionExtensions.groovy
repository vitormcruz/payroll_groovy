package com.vmc.payroll.external.presentation.converter

import com.cedarsoftware.util.io.JsonWriter
import com.vmc.payroll.domain.Employee

class ObjectJsonConversionExtensions {

    static String asJson(Object object){
        object.asJsonConverter().toJson()
    }

    static JsonConverter asJsonConverter(Object object){
        return new DefaultJsonConverter(object)
    }

    static JsonConverter asJsonConverter(Employee employee){
        return new EmployeeJsonDTO(employee)
    }

    static class DefaultJsonConverter implements JsonConverter{

        private Object object

        DefaultJsonConverter(Object object) {
            this.object = object
        }

        @Override
        String toJson() {
            return JsonWriter.objectToJson(object)
        }
    }
}
