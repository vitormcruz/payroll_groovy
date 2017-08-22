package com.vmc.payroll.domain.unionAssociation.api

import com.vmc.payroll.domain.payment.attachment.api.UnionCharge
import com.vmc.payroll.domain.payment.attachment.api.PaymentAttachment

interface UnionAssociation {

    Integer getRate()

    void setRate(Integer newRate)

    def getEmployee()

    Boolean isUnionMember()

    void postPaymentAttachment(PaymentAttachment paymentAttachment)

    Collection<UnionCharge> getCharges()
}