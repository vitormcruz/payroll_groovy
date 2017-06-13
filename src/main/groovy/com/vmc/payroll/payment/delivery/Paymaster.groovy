package com.vmc.payroll.payment.delivery

import com.vmc.payroll.Employee
import com.vmc.payroll.payment.delivery.api.PaymentDelivery

import static com.google.gwt.core.shared.impl.InternalPreconditions.checkArgument

class Paymaster implements PaymentDelivery{

    private Employee employee

    static Paymaster newPaymentDelivery(Employee employee){
        return new Paymaster(employee)
    }

    protected Paymaster() {
    }

    protected Paymaster(Employee employee) {
        checkArgument(employee != null, "Did you miss passing my employee?")
        this.employee = employee
    }

    @Override
    Employee getEmployee() {
        return employee
    }
}
