package com.vmc.payroll.domain

import com.vmc.payroll.domain.api.Entity
import com.vmc.payroll.domain.payment.attachment.api.PaymentAttachment
import com.vmc.payroll.domain.payment.delivery.api.PaymentDelivery
import com.vmc.payroll.domain.payment.type.api.PaymentType
import com.vmc.payroll.domain.unionAssociation.BasicUnionAssociation
import com.vmc.payroll.domain.unionAssociation.NoUnionAssociation
import com.vmc.payroll.domain.unionAssociation.api.UnionAssociation
import com.vmc.validationNotification.Mandatory

import static com.vmc.validationNotification.Validate.validate
import static com.vmc.validationNotification.Validate.validateNewObject

class Employee implements Entity{

    private String id = UUID.randomUUID()

    protected Mandatory<String> name = new Mandatory<String>(null, "The employee name is required", [property:"name"])
    protected Mandatory<String> address = new Mandatory<String>(null, "The employee address is required", [property:"address"])
    protected Mandatory<String> email = new Mandatory<String>(null, "The employee email is required", [property:"email"])
    protected PaymentType paymentType
    protected PaymentDelivery paymentDelivery
    protected UnionAssociation unionAssociation = NoUnionAssociation.getInstance()
    protected WeakHashMap paymentAttachmentListeners = new WeakHashMap()
    protected Set<PaymentAttachment> paymentAttachments = []

    static newEmployee(String name, String address, String email, paymentTypeProvider, paymentDeliveryProvider) {
        return validateNewObject(Employee, {new Employee(name, address, email, paymentTypeProvider, paymentDeliveryProvider)})
    }

    //For reflection magic only
    Employee() {}

    protected Employee(String name, String address, String email, paymentTypeProvider, paymentDeliveryProvider) {
        validate {initialize(name, address, email, paymentTypeProvider, paymentDeliveryProvider)}
    }

    void initialize(String name, String address, String email, paymentTypeProvider, paymentDeliveryProvider) {
        setName(name)
        setAddress(address)
        setEmail(email)
        bePaid(paymentTypeProvider)
        receivePaymentBy(paymentDeliveryProvider)
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

    /**
     * Tell's me that I should be paid with the paymentType provided by the paymentTypeProvider closure. I will call
     * the closure passing myself as argument so that the payment type provided can refer to myself if needed.
     */
    void bePaid(paymentTypeProvider){
        paymentTypeProvider ? paymentType = paymentTypeProvider(this) :
                            issueError("The employee payment type is required", [property:"payment.type"])
    }

    /**
     * Tell's me that I should receive my payment by the payment delivery provided by the paymentDeliveryProvider
     * closure. I will call the closure passing myself as argument so that the payment delivery provided can refer to
     * myself if needed.
     */
    void receivePaymentBy(paymentDeliveryProvider){
        paymentDeliveryProvider ? paymentDelivery = paymentDeliveryProvider(this) :
            issueError("The employee payment delivery is required", [property:"payment.delivery"])
    }

    /**
     *  Register as a payment attachment post listener notifying every attachment already posted to the new listener.
     */
    void registerAsPaymentAttachmentPostListener(listener) {
        paymentAttachments.each {listener.postPaymentAttachment(it)}
        paymentAttachmentListeners.put(listener, void)
    }

    void deRegisterAsPaymentAttachmentPostListener(handler) {
        paymentAttachmentListeners.remove(handler)
    }

    /**
     * Payment attachment is anything that
     * @param paymentAttachment
     */
    void postPaymentAttachment(PaymentAttachment paymentAttachment){
        paymentAttachments.add(paymentAttachment)
        paymentAttachmentListeners.keySet().each {it.postPaymentAttachment(paymentAttachment)}
    }

    /**
     * Return all my payment attachments. Consider registerAsPaymentAttachmentPostListener instead of using
     * this method.
     */
    def getPaymentAttachments(){
        return new HashSet(paymentAttachments)
    }

    void beUnionMember(Integer rate) {
        unionAssociation = BasicUnionAssociation.newUnionAssociation(this, rate)
    }

    void dropUnionMembership() {
        unionAssociation = NoUnionAssociation.getInstance()
    }

    UnionAssociation getUnionAssociation() {
        return unionAssociation
    }

    Boolean isUnionMember() {
        unionAssociation.isUnionMember()
    }
}
