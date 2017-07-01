package com.vmc.payroll.api

import com.vmc.payroll.Employee
import com.vmc.payroll.payment.delivery.api.PaymentDelivery
import com.vmc.payroll.payment.type.api.PaymentType


interface EmployeeRepository extends Repository<Employee>{

    Collection<Class<PaymentType>> getAllPaymentTypes()

    Collection<Class<PaymentDelivery>> getAllPaymentDelivery()
}