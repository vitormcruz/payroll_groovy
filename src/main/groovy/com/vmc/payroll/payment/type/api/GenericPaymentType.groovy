package com.vmc.payroll.payment.type.api

import com.vmc.payroll.payment.workEvent.api.WorkDoneProof
import com.vmc.payroll.payment.workEvent.api.WorkEvent

import static com.google.common.base.Preconditions.checkArgument

abstract class GenericPaymentType implements PaymentType{

    protected employee

    protected Set<WorkDoneProof> paymentAttachments = new HashSet<WorkDoneProof>()

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
        if(workEvent instanceof WorkDoneProof){
            addPaymentAttachment(workEvent)
        }
    }

    void addPaymentAttachment(WorkDoneProof paymentAttachment){
        paymentAttachments.add(paymentAttachment)
    }

    Set<WorkDoneProof> getPaymentAttachments(){
        return new HashSet<WorkDoneProof>(paymentAttachments)
    }

}
