package com.vmc.payroll.payment.delivery

import com.vmc.payroll.Employee


class PaymasterDelivery implements PaymentDelivery{

    private Employee employee

    static PaymasterDelivery newPaymentDelivery(Employee employee){
        return new PaymasterDelivery(employee)
    }

    protected PaymasterDelivery() {
    }

    protected PaymasterDelivery(Employee employee) {
        this.employee = employee
    }

}
