package com.vmc.payroll.domain.payment.type.api

import com.vmc.payroll.domain.payment.paymentAttachment.api.WorkDoneProof
import com.vmc.payroll.domain.payment.paymentAttachment.api.PaymentAttachment

/**
 * I represent various payment types, such as by commission or monthly.
 */
interface PaymentType {

    def getEmployee()

    void postPaymentAttachment(PaymentAttachment paymentAttachment)

    Set<WorkDoneProof> getPaymentAttachments()
}