package com.vmc.payroll.external.presentation.converter

import com.vmc.payroll.domain.payment.type.Monthly

class PaymentTypeConverterExtensions {

    static fillConverterWithParams(Monthly monthly, EmployeeJsonDTO employeeConverter){
        employeeConverter.paymentType = Monthly.simpleName
        employeeConverter.salary = monthly.salary
    }
}
