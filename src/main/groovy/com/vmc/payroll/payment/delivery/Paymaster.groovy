package com.vmc.payroll.payment.delivery

import com.vmc.payroll.payment.delivery.api.PaymentDelivery

import static com.google.gwt.core.shared.impl.InternalPreconditions.checkArgument

class Paymaster implements PaymentDelivery{

    private employee

    static Paymaster newPaymentDelivery(employee){
        return new Paymaster(employee)
    }

    protected Paymaster() {
    }

    protected Paymaster(employee) {
        checkArgument(employee != null, "Did you miss passing my employee?")
        this.employee = employee
    }

    @Override
    def getEmployee() {
        return employee
    }
}
