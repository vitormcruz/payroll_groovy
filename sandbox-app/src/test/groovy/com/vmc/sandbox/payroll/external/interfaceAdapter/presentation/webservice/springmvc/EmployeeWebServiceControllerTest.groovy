package com.vmc.sandbox.payroll.external.interfaceAdapter.presentation.webservice.springmvc

import com.vmc.sandbox.payroll.external.presentation.webservice.springmvc.EmployeeWebServiceController
import org.junit.Ignore
import org.junit.Test
import com.vmc.sandbox.payroll.external.config.sevletContextConfig.ContextConfigListener
import com.vmc.sandbox.validationNotification.ApplicationValidationNotifier

/**
 * Created by UR5Y on 01/07/2016.
 */
@Ignore
class EmployeeWebServiceControllerTest {

    @Test
    def void abc(){
        ApplicationValidationNotifier.createCurrentListOfListeners()
        new ContextConfigListener().contextInitialized(null)
        ApplicationValidationNotifier.destroyCurrentListOfListeners()

        ApplicationValidationNotifier.createCurrentListOfListeners()
//        new EmployeeRestController().newEmployee("{\n" +
//                "   \"name\": \"Joao1\",\n" +
//                "   \"address\" : \"Rua2\",\n" +
//                "   \"email\" : \"blabla@gmail.com\",\n" +
//                "   \"paymentMethod\": {\"salary\": \"5000\"}\n" +
//                "}")

        new EmployeeWebServiceController().newEmployee(new HashMap<String, String>(){{
            put("name", "Joao1")
            put("address", "Rua2")
            put("email", "blabla@gmail.com")
            put("paymentMethod", ["value": "5000"])
        }})

        ApplicationValidationNotifier.destroyCurrentListOfListeners()
    }
}