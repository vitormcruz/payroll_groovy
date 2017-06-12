package com.vmc.payroll

import com.vmc.payroll.payment.attachment.WorkEvent
import com.vmc.payroll.payment.delivery.PaymentDelivery
import com.vmc.payroll.payment.type.PaymentType
import com.vmc.payroll.unionAssociation.DefaultUnionAssociation
import com.vmc.payroll.unionAssociation.NoUnionAssociation
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
    protected Employee(String name, String address, String email, paymentArgs, paymentDeliveryClass) {
        executeNamedValidation("Validate new Employee", {
            setName(name)
            setAddress(address)
            setEmail(email)
            bePaid(*paymentArgs)
            receivePaymentBy(paymentDeliveryClass)
        })
    }

    @Override
    def getId() {
        return id
    }

    public void setName(String aName) {
        if(aName == null){
            issueError(this, [name:"employee.name"], "payroll.employee.name.mandatory")
            return
        }

        this.@name = aName
    }

    public void setAddress(String anAddress) {
        if(anAddress == null){
            issueError(this, [name:"employee.address"], "payroll.employee.address.mandatory")
            return
        }

        this.@address = anAddress
    }

    public void setEmail(String anEmail) {
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

    public void bePaid(Class<PaymentType> aPaymentTypeClass, ...args){
        if(aPaymentTypeClass == null || args == null || (args as List).isEmpty()){
            issueError(this, [name:"employee.payment.type"], "payroll.employee.payment.type.mandatory")
            return
        }
        paymentType = aPaymentTypeClass.newPaymentType(this, *args)
    }

    public void receivePaymentBy(Class<PaymentDelivery> aPaymentDeliveryClass){
        if(aPaymentDeliveryClass == null){
            issueError(this, [name:"employee.payment.delivery"], "payroll.employee.payment.delivery.mandatory")
            return
        }
        paymentDelivery = aPaymentDeliveryClass.newPaymentDelivery(this)
    }

    public void postWorkEvent(WorkEvent workEvent){
        workEventHandlers.each {it.postWorkEvent(workEvent)}
    }

    public void registerAsWorkEventHandler(handler) {
        workEventHandlers.add(handler)
    }

    public void beUnionMember(Integer rate) {
        unionAssociation = DefaultUnionAssociation.newUnionAssociation(this, rate)
    }

    UnionAssociation getUnionAssociation() {
        return unionAssociation
    }

    public getPaymentAttachments(){
        return paymentType.getPaymentAttachments()
    }

    public Boolean isUnionMember() {
        unionAssociation.isUnionMember()
    }

    public void dropUnionMembership() {
        unionAssociation = NoUnionAssociation.getInstance()
    }
}
