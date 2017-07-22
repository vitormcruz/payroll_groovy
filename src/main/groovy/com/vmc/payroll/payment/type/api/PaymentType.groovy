package com.vmc.payroll.payment.type.api

import com.vmc.payroll.payment.paymentAttachment.api.WorkDoneProof
import com.vmc.payroll.payment.paymentAttachment.api.PaymentAttachment

interface PaymentType {

    def getEmployee()

    void postPaymentAttachment(PaymentAttachment paymentAttachment)

    Set<WorkDoneProof> getPaymentAttachments()
}