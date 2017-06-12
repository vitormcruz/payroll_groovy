package com.vmc.payroll.payment.delivery

import com.vmc.payroll.Employee


class AccountTransferDelivery implements PaymentDelivery{

    private Employee employee

    static PaymasterDelivery newPaymentDelivery(Employee employee){
        return new PaymasterDelivery(employee)
    }

    protected AccountTransferDelivery() {
    }

    protected AccountTransferDelivery(Employee employee) {
        this.employee = employee
    }

}
