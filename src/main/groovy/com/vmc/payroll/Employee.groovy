package com.vmc.payroll

import com.vmc.payroll.api.Entity
import com.vmc.payroll.payment.delivery.api.PaymentDelivery
import com.vmc.payroll.payment.paymentAttachment.api.PaymentAttachment
import com.vmc.payroll.payment.type.api.PaymentType
import com.vmc.payroll.unionAssociation.BasicUnionAssociation
import com.vmc.payroll.unionAssociation.NoUnionAssociation
import com.vmc.payroll.unionAssociation.api.UnionAssociation
import com.vmc.validationNotification.Validate
import com.vmc.validationNotification.api.ConstructorValidator

import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation

class Employee implements Entity{

    private String id = UUID.randomUUID()

    private Mandatory<String> name = new Mandatory<String>(null, "The employee name is required", [property:"name"])
    private Mandatory<String> address = new Mandatory<String>(null, "The employee address is required", [property:"address"])
    private Mandatory<String> email = new Mandatory<String>(null, "The employee email is required", [property:"email"])
    private PaymentType paymentType
    private PaymentDelivery paymentDelivery
    private UnionAssociation unionAssociation = NoUnionAssociation.getInstance()
    private paymentAttachmentHandlers = []

    static newEmployee(String name, String address, String email, paymentTypeProvider, paymentDeliveryProvider) {
        return Validate.validate(Employee, {new Employee(name, address, email, paymentTypeProvider, paymentDeliveryProvider)})
    }

    //For reflection magic only
    Employee() {}

    protected Employee(String name, String address, String email, paymentTypeProvider, paymentDeliveryProvider) {
        def constructorValidator = new ConstructorValidator()
        prepareNewEmployee(name, address, email, paymentTypeProvider, paymentDeliveryProvider)
        constructorValidator.validateConstruction()
    }

    void prepareNewEmployee(String name, String address, String email, paymentTypeProvider, paymentDeliveryProvider) {
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

    String getName() {
        return name.get()
    }

    void setName(String aName) {
        this.@name.set(aName)
    }

    String getAddress() {
        return address.get()
    }

    void setAddress(String anAddress) {
        this.@address.set(anAddress)
    }

    String getEmail() {
        return email.get()
    }

    void setEmail(String anEmail) {
        this.@email.set(anEmail)
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

    void postPaymentAttachment(PaymentAttachment paymentAttachment){
        paymentAttachmentHandlers.each {it.postPaymentAttachment(paymentAttachment)}
    }

    void registerAsPaymentAttachmentHandler(handler) {
        paymentAttachmentHandlers.add(handler)
    }

    def getPaymentAttachments(){
        return paymentType.getPaymentAttachments()
    }

    void beUnionMember(Integer rate) {
        unionAssociation = BasicUnionAssociation.newUnionAssociation(this, rate)
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
