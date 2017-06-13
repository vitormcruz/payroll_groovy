package com.vmc.payroll.unionAssociation

import com.vmc.payroll.Employee
import com.vmc.payroll.payment.workEvent.api.UnionCharge
import com.vmc.payroll.payment.workEvent.api.WorkEvent

interface UnionAssociation {

    Integer getRate()

    Employee getEmployee()

    Boolean isUnionMember()

    void postWorkEvent(WorkEvent workEvent)

    Collection<UnionCharge> getCharges()
}