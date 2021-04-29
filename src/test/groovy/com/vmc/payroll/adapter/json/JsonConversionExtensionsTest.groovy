package com.vmc.payroll.adapter.json

import com.cedarsoftware.util.io.JsonReader
import com.vmc.payroll.domain.EmployeeMother
import com.vmc.payroll.domain.unionAssociation.NoUnionAssociation
import org.apache.commons.lang3.builder.EqualsBuilder
import org.junit.Test

class JsonConversionExtensionsTest {

    static{
        JsonReader.assignInstantiator(NoUnionAssociation, {return NoUnionAssociation.getInstance()} as JsonReader.ClassFactory)
    }

    @Test
    void "Test serialization"(){
        def employee = EmployeeMother.randomEmployeeMother.createNewBorn()
        EqualsBuilder.reflectionEquals(employee, JsonReader.jsonToJava(employee.toJson()), "paymentAttachmentListeners")
    }

    @Test
    void "paymentAttachmentListeners should not be serialized"(){
        assert !EmployeeMother.randomEmployeeMother.createNewBorn().toJson().contains("paymentAttachmentListeners")
    }

}
