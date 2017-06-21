package com.vmc.payroll.external.persistence.inMemory

import com.vmc.payroll.Employee
import com.vmc.payroll.api.EmployeeRepository
import com.vmc.payroll.external.persistence.inMemory.repository.CommonInMemoryRepository
import com.vmc.payroll.payment.type.api.GenericPaymentType
import com.vmc.payroll.payment.type.api.PaymentType
import org.reflections.Reflections

class InMemoryEmployeeRepository extends CommonInMemoryRepository<Employee> implements EmployeeRepository {

    private static Set<Class> paymentTypes

    static {
        paymentTypes = new Reflections("com.vmc.payroll.payment.type").getSubTypesOf(PaymentType)
        paymentTypes.remove(GenericPaymentType)
    }

    @Override
    Collection<Class<PaymentType>> getAllPaymentTypes() {
        return paymentTypes
    }
}
