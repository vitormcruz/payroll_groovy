package com.vmc.payroll.payment.type.api

import com.vmc.payroll.payment.paymentAttachment.api.WorkDoneProof
import com.vmc.payroll.payment.paymentAttachment.api.PaymentAttachment

interface PaymentType {

    def getEmployee()

    /**
     * Post an work event
     */
    void postPaymentAttachment(PaymentAttachment workEvent)

    Set<WorkDoneProof> getPaymentAttachments()
}