package com.vmc.payroll.payment.delivery

import com.vmc.payroll.Employee
import com.vmc.payroll.payment.delivery.api.PaymentDelivery
import com.vmc.validationNotification.builder.BuilderAwareness
import com.vmc.validationNotification.builder.imp.GenericBuilder

import static com.google.gwt.core.shared.impl.InternalPreconditions.checkArgument
import static com.vmc.validationNotification.ApplicationValidationNotifier.issueError

class AccountTransferDelivery implements PaymentDelivery, BuilderAwareness{

    private Employee employee
    String bank
    String account

    //Should be used by builder only
    static AccountTransferDelivery newPaymentDelivery(Employee employee, String bank, String account){
        return new GenericBuilder(AccountTransferDelivery).withEmployee(employee).withBank(bank).withAccount(account).build()
    }

    protected AccountTransferDelivery() {
        //Available only for reflection magic
        invalidForBuilder()
    }

    protected AccountTransferDelivery(Employee anEmployee, String aBank, String anAccount) {
        checkArgument(anEmployee != null, "Did you miss passing my employee?")
        this.employee = anEmployee
        setBank(aBank)
        setAccount(anAccount)
    }

    @Override
    Employee getEmployee() {
        return employee
    }

    void setBank(String aBank) {
        aBank ? this.@bank = aBank : issueError(this, [name: "bank"], "payroll.account.transfer.delivery.bank.mandatory")
    }

    void setAccount(String anAccount) {
        anAccount ? this.account = anAccount : issueError(this, [name: "bank"], "payroll.account.transfer.delivery.account.mandatory")
    }
}
