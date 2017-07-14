package com.vmc.payroll.external.presentation.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.vmc.payroll.Employee
import com.vmc.payroll.external.config.ServiceLocator
import com.vmc.payroll.payment.type.api.PaymentType
import com.vmc.validationNotification.builder.GenericBuilder
import org.apache.commons.lang.StringUtils
import org.reflections.Reflections

import static com.google.common.base.Preconditions.checkArgument

class EmployeeJsonConverter implements JsonConverter{

    private static ObjectMapper mapper = ServiceLocator.getInstance().mapper()
    private static Set<Class> paymentTypes = new Reflections("com.vmc.payroll.payment.type").getSubTypesOf(PaymentType)

    String id
    String name
    String address
    String email
    String paymentType
    Class<PaymentType> paymentTypeClass
    Integer salary
    Integer commissionRate
    Integer hourRate
    Integer rate

    EmployeeJsonConverter() {
    }

    EmployeeJsonConverter(Employee employee) {
        id = employee.id
        name = employee.name
        address = employee.address
        employee.paymentType.fillConverterWithParams(this)

    }

    static builderFromJson(String string){
        def employeeConverter = mapper.readValue(string, EmployeeJsonConverter)
        checkArgument(employeeConverter.paymentType != null, "Json formmat is invalid: you must specify a payment type of one of the following alternatives:" +
                                                                            StringUtils.join(paymentTypes.collect {it.getSimpleName()}, ", "))
        return employeeConverter.meToBuilder()

    }

    GenericBuilder meToBuilder() {
        return new GenericBuilder(Employee).withName(name).withAddress(address).withEmail(email).withPayment(paymentTypeClass.paramsFromConverter(this))
    }

    String getPaymentType() {
        return paymentType
    }

    void setPaymentType(String aPaymentType) {
        paymentType = aPaymentType
        paymentTypeClass = paymentTypes.find { it.getSimpleName().equalsIgnoreCase(aPaymentType) }
        checkArgument(paymentTypeClass!=null, "Json formmat is invalid: you must specify a payment type of one of the following alternatives:" +
                                                            StringUtils.join(paymentTypes.collect {it.getSimpleName()}, ", "))
    }

    @Override
    String toJson() {
        return mapper.writeValueAsString(this)
    }
}
