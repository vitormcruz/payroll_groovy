package com.vmc.payroll.domain.unionAssociation

import com.vmc.payroll.domain.payment.paymentAttachment.api.PaymentAttachment
import com.vmc.payroll.domain.payment.paymentAttachment.api.UnionCharge
import com.vmc.payroll.domain.unionAssociation.api.UnionAssociation
import com.vmc.validationNotification.objectCreation.ConstructorValidator

import static com.google.common.base.Preconditions.checkArgument
import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation
import static com.vmc.validationNotification.Validate.validate

class BasicUnionAssociation implements UnionAssociation{

    private Integer rate
    private employee
    private charges = []

    static BasicUnionAssociation newUnionAssociation(employee, Integer aRate){
        return validate(BasicUnionAssociation, { new BasicUnionAssociation(employee, aRate)})
    }

    BasicUnionAssociation() {
    }

    BasicUnionAssociation(anEmployee, Integer aRate) {
        def constructorValidator = new ConstructorValidator()
        initialize(anEmployee, aRate)
        constructorValidator.validateConstruction()
    }

    void initialize(anEmployee, Integer aRate) {
        checkArgument(anEmployee != null, "An Employee should be provided to a Default Union Association")
        this.employee = anEmployee
        this.employee.registerAsPaymentAttachmentHandler(this)
        executeNamedValidation("Validate new Basic Union Association", { setRate(aRate) })
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
