package com.vmc.payroll

import com.vmc.payroll.api.Entity
import com.vmc.payroll.payment.attachment.api.WorkEvent
import com.vmc.payroll.payment.delivery.api.PaymentDelivery
import com.vmc.payroll.payment.type.api.PaymentType
import com.vmc.payroll.unionAssociation.imp.DefaultUnionAssociation
import com.vmc.payroll.unionAssociation.imp.NoUnionAssociation
import com.vmc.payroll.unionAssociation.UnionAssociation
import com.vmc.validationNotification.builder.BuilderAwareness

import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation
import static com.vmc.validationNotification.ApplicationValidationNotifier.issueError

class Employee implements Entity, BuilderAwareness{

    private String id = UUID.randomUUID()

    String name
    String address
    String email
    private PaymentType paymentType
    private PaymentDelivery paymentDelivery
    private UnionAssociation unionAssociation = NoUnionAssociation.getInstance()
    private workEventHandlers = []

    private Employee() {
        //Available only for reflection magic
        invalidForBuilder()
    }

    //Should be used by builder only
    protected Employee(String name, String address, String email, paymentArgs, paymentDeliveryArgs) {
        executeNamedValidation("Validate new Employee", {
            setName(name)
            setAddress(address)
            setEmail(email)
            bePaid(*toSpreadable(paymentArgs))
            receivePaymentBy(*toSpreadable(paymentDeliveryArgs))
        })
    }

    def toSpreadable(possibleSpreadable){
        def spreadable = [possibleSpreadable].flatten()
        return spreadable
    }

    @Override
    def getId() {
        return id
    }

    void setName(String aName) {
        if(aName == null){
            issueError(this, [name:"employee.name"], "payroll.employee.name.mandatory")
            return
        }

        this.@name = aName
    }

    void setAddress(String anAddress) {
        if(anAddress == null){
            issueError(this, [name:"employee.address"], "payroll.employee.address.mandatory")
            return
        }

        this.@address = anAddress
    }

    void setEmail(String anEmail) {
        if(anEmail == null){
            issueError(this, [name:"employee.email"], "payroll.employee.email.mandatory")
            return
        }

        this.@email = anEmail
    }

    PaymentType getPaymentType() {
        return paymentType
    }

    PaymentDelivery getPaymentDelivery() {
        return paymentDelivery
    }

    void bePaid(Class<PaymentType> aPaymentTypeClass, ...args){
        if(aPaymentTypeClass == null || args == null || (args as List).isEmpty()){
            issueError(this, [name:"employee.payment.type"], "payroll.employee.payment.type.mandatory")
            return
        }
        paymentType = aPaymentTypeClass.newPaymentType(this, *args)
    }

    void receivePaymentBy(Class<PaymentDelivery> aPaymentDeliveryClass, ...args){
        if(aPaymentDeliveryClass == null){
            issueError(this, [name:"employee.payment.delivery"], "payroll.employee.payment.delivery.mandatory")
            return
        }
        paymentDelivery = aPaymentDeliveryClass.newPaymentDelivery(this, *args)
    }

    void postWorkEvent(WorkEvent workEvent){
        workEventHandlers.each {it.postWorkEvent(workEvent)}
    }

    void registerAsWorkEventHandler(handler) {
        workEventHandlers.add(handler)
    }

    def getPaymentAttachments(){
        return paymentType.getPaymentAttachments()
    }

    void beUnionMember(Integer rate) {
        unionAssociation = DefaultUnionAssociation.newUnionAssociation(this, rate)
    }

    UnionAssociation getUnionAssociation() {
        return unionAssociation
    }

    Boolean isUnionMember() {
        unionAssociation.isUnionMember()
    }

    void dropUnionMembership() {
        unionAssociation = NoUnionAssociation.getInstance()
    }
}
