package com.vmc.payroll.payment.type.api

import com.vmc.payroll.payment.workEvent.api.PaymentAttachment
import com.vmc.payroll.payment.workEvent.api.WorkEvent

import static com.google.common.base.Preconditions.checkArgument

abstract class GenericPaymentType implements PaymentType{

    protected employee

    protected Set<PaymentAttachment> paymentAttachments = new HashSet<PaymentAttachment>()

    private GenericPaymentType() {}

    GenericPaymentType(anEmployee){
        checkArgument(anEmployee != null, "Employee must be provided for payment types, but I got it null")
        this.employee = anEmployee
        anEmployee.registerAsWorkEventHandler(this)
    }

    @Override
    def getEmployee() {
        return employee
    }

    @Override
    void postWorkEvent(WorkEvent workEvent) {
        if(workEvent instanceof PaymentAttachment){
            addPaymentAttachment(workEvent)
        }
    }

    void addPaymentAttachment(PaymentAttachment paymentAttachment){
        paymentAttachments.add(paymentAttachment)
    }

    Set<PaymentAttachment> getPaymentAttachments(){
        return new HashSet<PaymentAttachment>(paymentAttachments)
    }

}
