package com.vmc.payroll.domain

import com.vmc.payroll.domain.api.EntityCommonTrait
import com.vmc.payroll.domain.payment.attachment.api.PaymentAttachment
import com.vmc.payroll.domain.payment.delivery.api.PaymentDelivery
import com.vmc.payroll.domain.payment.type.api.PaymentType
import com.vmc.payroll.domain.unionAssociation.BasicUnionAssociation
import com.vmc.payroll.domain.unionAssociation.NoUnionAssociation
import com.vmc.payroll.domain.unionAssociation.api.UnionAssociation

import static com.vmc.validationNotification.Validation.validate
import static com.vmc.validationNotification.Validation.validateNewObject

class Employee implements EntityCommonTrait{

    private String id = UUID.randomUUID()

    protected String name
    protected String address
    protected String email

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
        return this.@name
    }

    void setName(String aName) {
        if(aName == null) {
            issueError("The employee name is required", [property: "name"])
            return
        }
        this.@name = aName
    }

    String getAddress() {
        this.@address
    }

    void setAddress(String anAddress) {
        if(anAddress == null) {
            issueError("The employee address is required", [property: "address"])
            return
        }
        this.@address = anAddress
    }

    String getEmail() {
        this.@email
    }

    void setEmail(String anEmail) {
        if(anEmail == null) {
            issueError("The employee email is required", [property: "email"])
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

    /**
     * Tell's me that I should be paid with the paymentType provided by the paymentTypeBuilder closure. I will call
     * the closure passing myself as argument so that the payment type provided can refer to myself if needed.
     */
    void bePaid(paymentTypeBuilder){
        paymentTypeBuilder ? paymentType = paymentTypeBuilder(this) :
                issueError("The employee payment type is required", [property:"payment.type"])
    }

    /**
     * Tell's me that I should receive my payment by the payment delivery provided by the paymentDeliveryBuilder
     * closure. I will call the closure passing myself as argument so that the payment delivery provided can refer to
     * myself if needed.
     */
    void receivePaymentBy(paymentDeliveryBuilder){
        paymentDeliveryBuilder ? paymentDelivery = paymentDeliveryBuilder(this) :
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
     * Return all my payment attachments. Consider use registerAsPaymentAttachmentPostListener method instead.
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

    @Override
    def getEntityClass() {
        return Employee
    }
}
