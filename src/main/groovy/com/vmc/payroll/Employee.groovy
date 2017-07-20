package com.vmc.payroll

import com.vmc.payroll.api.Entity
import com.vmc.payroll.payment.workEvent.api.WorkEvent
import com.vmc.payroll.payment.delivery.api.PaymentDelivery
import com.vmc.payroll.payment.type.api.PaymentType
import com.vmc.payroll.unionAssociation.DefaultUnionAssociation
import com.vmc.payroll.unionAssociation.NoUnionAssociation
import com.vmc.payroll.unionAssociation.api.UnionAssociation
import com.vmc.validationNotification.builder.api.BuilderAwareness

import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation

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
    //TODO change payment args to a closure receiving the employee so to create a payment.
    protected Employee(String name, String address, String email, paymentTypeProvider, paymentDeliveryProvider) {
        executeNamedValidation("Validate new Employee", {
            setName(name)
            setAddress(address)
            setEmail(email)
            bePaid(paymentTypeProvider)
            receivePaymentBy(paymentDeliveryProvider)
        })
    }

    @Override
    def getId() {
        return id
    }

    void setName(String aName) {
        aName ? this.@name = aName : issueError("The employee name is required", [property:"name"])
    }

    void setAddress(String anAddress) {
        anAddress ? this.@address = anAddress : issueError("The employee address is required", [property:"address"])
    }

    void setEmail(String anEmail) {
        anEmail ? this.@email = anEmail : issueError("The employee email is required", [property:"email"])
    }

    PaymentType getPaymentType() {
        return paymentType
    }

    PaymentDelivery getPaymentDelivery() {
        return paymentDelivery
    }

    void bePaid(paymentTypeProvider){
        paymentTypeProvider ? paymentType = paymentTypeProvider(this) :
                            issueError("The employee payment type is required", [property:"payment.type"])
    }

    void receivePaymentBy(paymentDeliveryProvider){
        paymentDeliveryProvider ? paymentDelivery = paymentDeliveryProvider(this) :
            issueError("The employee payment delivery is required", [property:"payment.delivery"])
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
