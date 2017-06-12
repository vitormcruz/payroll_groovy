package com.vmc.payroll.payment.delivery

import com.vmc.payroll.Employee

import static com.google.gwt.core.shared.impl.InternalPreconditions.checkArgument

class PaymasterDelivery implements PaymentDelivery{

    private Employee employee

    static PaymasterDelivery newPaymentDelivery(Employee employee){
        return new PaymasterDelivery(employee)
    }

    protected PaymasterDelivery() {
    }

    protected PaymasterDelivery(Employee employee) {
        checkArgument(employee != null, "Did you miss passing my employee?")
        this.employee = employee
    }

    @Override
    Employee getEmployee() {
        return employee
    }
}
