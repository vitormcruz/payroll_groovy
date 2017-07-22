package com.vmc.payroll.unionAssociation.api

import com.vmc.payroll.payment.workEvent.api.UnionCharge
import com.vmc.payroll.payment.workEvent.api.PaymentAttachment

interface UnionAssociation {

    Integer getRate()

    void setRate(Integer newRate)

    def getEmployee()

    Boolean isUnionMember()

    void postPaymentAttachment(PaymentAttachment workEvent)

    Collection<UnionCharge> getCharges()
}