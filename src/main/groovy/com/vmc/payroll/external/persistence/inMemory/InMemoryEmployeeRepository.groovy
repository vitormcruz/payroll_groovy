package com.vmc.payroll.external.persistence.inMemory

import com.vmc.payroll.Employee
import com.vmc.payroll.api.EmployeeRepository
import com.vmc.payroll.external.persistence.inMemory.repository.CommonInMemoryRepository
import com.vmc.payroll.payment.delivery.api.PaymentDelivery
import com.vmc.payroll.payment.type.api.GenericPaymentType
import com.vmc.payroll.payment.type.api.PaymentType
import org.reflections.Reflections

class InMemoryEmployeeRepository extends CommonInMemoryRepository<Employee> implements EmployeeRepository {

    private static Set<Class> paymentTypeClasses
    private static Set<Class> paymentDeliveryClasses

    static {
        paymentTypeClasses = new Reflections("com.vmc.payroll.payment.type").getSubTypesOf(PaymentType)
        paymentTypeClasses.remove(GenericPaymentType)

        paymentDeliveryClasses = new Reflections("com.vmc.payroll.payment.delivery").getSubTypesOf(PaymentDelivery)
    }

    @Override
    Collection<Class<PaymentType>> getAllPaymentTypes() {
        return new HashSet<Class<PaymentType>>(paymentTypeClasses)
    }

    @Override
    Collection<Class<PaymentDelivery>> getAllPaymentDelivery() {
        return new HashSet<Class<PaymentDelivery>>(paymentDeliveryClasses)
    }
}
