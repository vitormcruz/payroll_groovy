package com.vmc.payroll.unionAssociation

import com.vmc.payroll.Employee
import com.vmc.payroll.payment.attachment.UnionCharge
import com.vmc.payroll.payment.attachment.WorkEvent

interface UnionAssociation {

    Integer getRate()

    Employee getEmployee()

    Boolean isUnionMember()

    void postWorkEvent(WorkEvent workEvent)

    Collection<UnionCharge> getCharges()
}