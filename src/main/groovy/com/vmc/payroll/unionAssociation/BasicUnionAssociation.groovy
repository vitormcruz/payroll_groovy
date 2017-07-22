package com.vmc.payroll.unionAssociation

import com.vmc.payroll.payment.paymentAttachment.api.PaymentAttachment
import com.vmc.payroll.payment.paymentAttachment.api.UnionCharge
import com.vmc.payroll.unionAssociation.api.UnionAssociation

import static com.google.common.base.Preconditions.checkArgument
import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation
import static com.vmc.validationNotification.Validate.validate

class BasicUnionAssociation implements UnionAssociation{

    private Integer rate
    private employee
    private charges = []

    static BasicUnionAssociation newUnionAssociation(employee, Integer aRate){
        return validate { new BasicUnionAssociation(employee, aRate)}
    }

    /**
     * Should be used for reflection magic only
     */
    BasicUnionAssociation() {
    }

    /**
     * Use newUnionAssociation instead, otherwise be careful as you can end up with an invalid object.
     */
    BasicUnionAssociation(anEmployee, Integer aRate) {
        checkArgument(anEmployee != null, "An Employee should be provided to a Default Union Association")
        this.employee = anEmployee
        this.employee.registerAsPaymentAttachmentHandler(this)
        executeNamedValidation("Validate new ServiceCharge", {
            setRate(aRate)
        })
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
