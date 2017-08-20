package com.vmc.payroll.external.presentation.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.vmc.payroll.domain.Employee
import com.vmc.payroll.external.config.ServiceLocator
import com.vmc.payroll.domain.payment.type.api.PaymentType
import org.apache.commons.lang.StringUtils
import org.reflections.Reflections

import static com.google.common.base.Preconditions.checkArgument

//TODO add tests
class EmployeeJsonDTO implements JsonConverter{

    private static ObjectMapper mapper = ServiceLocator.getInstance().mapper()
    private static Set<Class> paymentTypes = new Reflections("com.vmc.payroll.domain.payment.type").getSubTypesOf(PaymentType)

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

    EmployeeJsonDTO() {
    }

    EmployeeJsonDTO(Employee employee) {
        id = employee.id
        name = employee.name
        address = employee.address
        employee.paymentType.fillConverterWithParams(this)

    }

    static employeeFromJson(String string){
        def employeeConverter = mapper.readValue(string, EmployeeJsonDTO)
        checkArgument(employeeConverter.paymentType != null, "Json formmat is invalid: you must specify a payment type " +
                                                             "of one of the following alternatives:" +
                                                             StringUtils.join(paymentTypes.collect {it.getSimpleName()}, ", "))
        return employeeConverter.toEmployee()

    }

    Employee toEmployee() {
        return Employee.newEmployee(name, address, email, paymentTypeClass.paramsFromConverter(this), null)
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
