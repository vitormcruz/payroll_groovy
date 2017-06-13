package com.vmc.payroll.payment.type.api

import com.vmc.payroll.Employee
import com.vmc.payroll.payment.workEvent.api.PaymentAttachment
import com.vmc.payroll.payment.workEvent.api.WorkEvent

interface PaymentType {

    Employee getEmployee()

    void postWorkEvent(WorkEvent workEvent)

    Set<PaymentAttachment> getPaymentAttachments()
}