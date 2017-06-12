package com.vmc.payroll.payment.delivery

import com.vmc.payroll.Employee


class MailDelivery implements PaymentDelivery {

    private Employee employee

    static MailDelivery newPaymentDelivery(Employee employee){
        return new MailDelivery(employee)
    }

    protected MailDelivery() {
    }

    protected MailDelivery(Employee employee) {
        this.employee = employee
    }
}
