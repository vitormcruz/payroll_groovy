package com.vmc.payroll.domain.unionAssociation

import com.vmc.payroll.domain.payment.attachment.api.PaymentAttachment
import com.vmc.payroll.domain.payment.attachment.api.UnionCharge
import com.vmc.payroll.domain.unionAssociation.api.UnionAssociation

import static com.google.common.base.Preconditions.checkArgument
import static com.vmc.validationNotification.Validation.validate
import static com.vmc.validationNotification.Validation.validateNewObject

class BasicUnionAssociation implements UnionAssociation{

    protected Integer rate
    protected employee
    protected charges = []

    static BasicUnionAssociation newUnionAssociation(employee, Integer aRate){
        return validateNewObject(BasicUnionAssociation, { new BasicUnionAssociation(employee, aRate)})
    }

    //For reflection magic only
    BasicUnionAssociation() {
    }

    BasicUnionAssociation(anEmployee, Integer aRate) {
        validate {initialize(anEmployee, aRate)}
    }

    void initialize(anEmployee, Integer aRate) {
        checkArgument(anEmployee != null, "An Employee should be provided to a Default Union Association")
        this.employee = anEmployee
        this.employee.registerAsPaymentAttachmentPostListener(this)
        setRate(aRate)
    }

    @Override
    void postPaymentAttachment(PaymentAttachment paymentAttachment) {
        if(paymentAttachment instanceof UnionCharge){
            charges.add(paymentAttachment)
        }
    }

    @Override
    Collection<UnionCharge> getCharges() {
        return new ArrayList(charges)
    }

    @Override
    Integer getRate() {
        return rate
    }

    @Override
    void setRate(Integer newRate) {
        if(newRate == null){
            issueError("The union association rate is required for members", [property:"rate"])
        } else if(newRate < 1){
            issueError("The rate must be a positive integer", [property:"rate"])
        }else {
            this.@rate = newRate
        }
    }

    @Override
    def getEmployee() {
        return employee
    }

    @Override
    Boolean isUnionMember() {
        return true
    }
}
