package com.vmc.payroll.payment.delivery

import com.vmc.payroll.payment.delivery.api.PaymentDelivery
import com.vmc.validationNotification.builder.api.BuilderAwareness
import com.vmc.validationNotification.builder.GenericBuilder

import static com.google.common.base.Preconditions.checkArgument

class AccountTransfer implements PaymentDelivery, BuilderAwareness{

    private employee
    String bank
    String account

    //Should be used by builder only
    static AccountTransfer newPaymentDelivery(employee, String bank, String account){
        return new GenericBuilder(AccountTransfer).withEmployee(employee).withBank(bank).withAccount(account).build()
    }

    protected AccountTransfer() {
        //Available only for reflection magic
        invalidForBuilder()
    }

    protected AccountTransfer(anEmployee, String aBank, String anAccount) {
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
