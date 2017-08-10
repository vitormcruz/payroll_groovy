package com.vmc.payroll.payment.delivery

import com.vmc.payroll.payment.delivery.api.PaymentDelivery
import com.vmc.validationNotification.Validate
import com.vmc.validationNotification.api.ConstructorValidator

import static com.google.common.base.Preconditions.checkArgument

class AccountTransfer implements PaymentDelivery{

    private employee
    String bank
    String account

    static AccountTransfer newPaymentDelivery(employee, String bank, String account){
        return Validate.validate(AccountTransfer, {new AccountTransfer(employee, bank, account)})
    }

    AccountTransfer() {
    }

    AccountTransfer(anEmployee, String aBank, String anAccount) {
        def constructorValidator = new ConstructorValidator()
        prepareConstructor(anEmployee, aBank, anAccount)
        constructorValidator.validateConstruction()
    }

    void prepareConstructor(anEmployee, String aBank, String anAccount) {
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
