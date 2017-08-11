package com.vmc.payroll.domain.unionAssociation.api

import com.vmc.payroll.domain.payment.paymentAttachment.api.UnionCharge
import com.vmc.payroll.domain.payment.paymentAttachment.api.PaymentAttachment

interface UnionAssociation {

    Integer getRate()

    void setRate(Integer newRate)

    def getEmployee()

    Boolean isUnionMember()

    void postPaymentAttachment(PaymentAttachment paymentAttachment)

    Collection<UnionCharge> getCharges()
}