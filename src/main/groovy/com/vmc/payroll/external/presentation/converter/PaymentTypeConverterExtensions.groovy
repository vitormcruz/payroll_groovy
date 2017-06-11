package com.vmc.payroll.external.presentation.converter

import com.vmc.payroll.payment.type.Monthly

class PaymentTypeConverterExtensions {

    public static fillConverterWithParams(Monthly monthly, EmployeeJsonConverter employeeConverter){
        employeeConverter.paymentType = Monthly.simpleName
        employeeConverter.salary = monthly.salary
    }
}
