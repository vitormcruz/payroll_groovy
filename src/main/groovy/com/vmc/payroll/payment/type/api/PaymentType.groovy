package com.vmc.payroll.payment.type.api

import com.vmc.payroll.payment.workEvent.api.WorkDoneProof
import com.vmc.payroll.payment.workEvent.api.WorkEvent

interface PaymentType {

    def getEmployee()

    /**
     * Post an work event
     */
    void postWorkEvent(WorkEvent workEvent)

    Set<WorkDoneProof> getPaymentAttachments()
}