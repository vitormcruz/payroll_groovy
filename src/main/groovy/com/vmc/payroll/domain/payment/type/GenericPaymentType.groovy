package com.vmc.payroll.domain.payment.type


import com.vmc.payroll.domain.payment.attachment.api.PaymentAttachment
import com.vmc.payroll.domain.payment.attachment.api.WorkDoneProof
import com.vmc.payroll.domain.payment.type.api.PaymentType

import static com.google.common.base.Preconditions.checkArgument

abstract class GenericPaymentType implements PaymentType{

    protected employee
    protected Set<WorkDoneProof> paymentAttachments = new HashSet<WorkDoneProof>()

    //For reflection magic only
    GenericPaymentType() {}

    GenericPaymentType(anEmployee){
        initialize(anEmployee)
    }

    void initialize(anEmployee) {
        checkArgument(anEmployee != null, "Employee must be provided for payment types, but I got it null")
        this.employee = anEmployee
        anEmployee.registerAsPaymentAttachmentPostListener(this)
    }

    @Override
    def getEmployee() {
        return employee
    }

    @Override
    void postPaymentAttachment(PaymentAttachment paymentAttachment) {
        if(paymentAttachment instanceof WorkDoneProof){
            addPaymentAttachment(paymentAttachment)
        }
    }

    void addPaymentAttachment(WorkDoneProof paymentAttachment){
        paymentAttachments.add(paymentAttachment)
    }

    Set<WorkDoneProof> getPaymentAttachments(){
        return new HashSet<WorkDoneProof>(paymentAttachments)
    }

}
