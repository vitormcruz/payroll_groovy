package com.vmc.payroll.payment.delivery

import com.vmc.payroll.Employee


class AccountTransferDelivery implements PaymentDelivery{

    private Employee employee

    static AccountTransferDelivery newPaymentDelivery(Employee employee){
        return new AccountTransferDelivery(employee)
    }

    protected AccountTransferDelivery() {
    }

    protected AccountTransferDelivery(Employee employee) {
        this.employee = employee
    }

    @Override
    Employee getEmployee() {
        return employee
    }
}
