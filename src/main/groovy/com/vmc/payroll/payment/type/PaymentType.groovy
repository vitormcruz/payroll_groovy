package com.vmc.payroll.payment.type

import com.vmc.payroll.Employee
import com.vmc.payroll.payment.attachment.PaymentAttachment
import com.vmc.payroll.payment.attachment.WorkEvent

interface PaymentType {

    Employee getEmployee()

    void postWorkEvent(WorkEvent workEvent)

    Set<PaymentAttachment> getPaymentAttachments()
}