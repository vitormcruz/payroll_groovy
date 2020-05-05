package com.vmc.payroll.domain.payment.delivery


import com.vmc.payroll.domain.payment.delivery.api.PaymentDelivery

import static com.google.common.base.Preconditions.checkArgument
import static com.vmc.validationNotification.Validation.validateNewObject

class Paymaster implements PaymentDelivery{

    private employee

    static Paymaster newPaymentDelivery(employee){
        return validateNewObject(Paymaster, {new Paymaster(employee)})
    }

    //For reflection magic only
    Paymaster() {
    }

    Paymaster(employee) {
        checkArgument(employee != null, "Did you miss passing my employee?")
        this.employee = employee
    }

    @Override
    def getEmployee() {
        return employee
    }
}
