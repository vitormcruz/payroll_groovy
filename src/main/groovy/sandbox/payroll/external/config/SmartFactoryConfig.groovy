package sandbox.payroll.external.config

import sandbox.payroll.payment.type.Monthly
import sandbox.simpleConverter.SimpleObjectMapping
import sandbox.smartfactory.SmartFactory
import sandbox.validationNotification.builder.imp.GenericBuilder

class SmartFactoryConfig implements Config {

    private smartFactory = SmartFactory.instance()

    @Override
    public void  configure() {
        generalAppConfig()
    }

    private void generalAppConfig() {
        //TODO change to payroll
        def globalConfiguration = smartFactory.configurationFor("**")
        SimpleObjectMapping objectMapping = new SimpleObjectMapping()
        def objectMappingForBuilder = objectMapping.getObjectMappingFor(GenericBuilder)
        dynamicMappingForEmployee(objectMappingForBuilder)
        globalConfiguration.put(SimpleObjectMapping, objectMapping)
    }

    private Object dynamicMappingForEmployee(LinkedHashMap objectMappingForBuilder) {
        objectMappingForBuilder.put("paymentMethod", { employeeBuilder, paymentMethodMap ->
            employeeBuilder.setPaymentType(new Monthly(Integer.valueOf(paymentMethodMap.get("salary"))))
        })
    }

    @Override
    public void tearDown() {
    }
}