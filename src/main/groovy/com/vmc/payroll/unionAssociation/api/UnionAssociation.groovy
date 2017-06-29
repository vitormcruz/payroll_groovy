package com.vmc.payroll.unionAssociation.api

import com.vmc.payroll.payment.workEvent.api.UnionCharge
import com.vmc.payroll.payment.workEvent.api.WorkEvent

interface UnionAssociation {

    Integer getRate()

    void setRate(Integer newRate)

    def getEmployee()

    Boolean isUnionMember()

    void postWorkEvent(WorkEvent workEvent)

    Collection<UnionCharge> getCharges()
}