package com.vmc.payroll.domain.payment.delivery


import com.vmc.payroll.domain.payment.delivery.api.PaymentDelivery

import static com.google.common.base.Preconditions.checkArgument
import static com.vmc.validationNotification.Validation.validate
import static com.vmc.validationNotification.Validation.validateNewObject

class AccountTransfer implements PaymentDelivery{

    protected employee
    String bank
    String account

    static AccountTransfer newPaymentDelivery(employee, String bank, String account){
        return validateNewObject(AccountTransfer, {new AccountTransfer(employee, bank, account)})
    }

    //For reflection magic only
    AccountTransfer() {
    }

    AccountTransfer(anEmployee, String aBank, String anAccount) {
        validate {initialize(anEmployee, aBank, anAccount)}
    }

    void initialize(anEmployee, String aBank, String anAccount) {
        checkArgument(anEmployee != null, "Did you miss passing my employee?")
        this.employee = anEmployee
        setBank(aBank)
        setAccount(anAccount)
    }

    @Override
    def getEmployee() {
        return employee
    }

    void setBank(String aBank) {
        aBank ? this.@bank = aBank : issueError("The bank is required", [property: "bank"])
    }

    void setAccount(String anAccount) {
        anAccount ? this.account = anAccount : issueError("The account is required", [property: "account"])
    }
}
