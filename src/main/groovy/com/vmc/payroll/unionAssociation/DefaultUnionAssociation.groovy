package com.vmc.payroll.unionAssociation

import com.vmc.payroll.Employee
import com.vmc.payroll.payment.attachment.UnionCharge
import com.vmc.payroll.payment.attachment.WorkEvent
import com.vmc.validationNotification.builder.imp.GenericBuilder

import static com.google.common.base.Preconditions.checkArgument
import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation
import static com.vmc.validationNotification.ApplicationValidationNotifier.issueError

class DefaultUnionAssociation implements UnionAssociation{

    private Integer rate
    private Employee employee
    private charges = []

    protected DefaultUnionAssociation(Employee anEmployee, Integer aRate) {
        checkArgument(anEmployee != null, "An Employee should be provided to a Default Union Association")
        this.employee = anEmployee
        this.employee.registerAsWorkEventHandler(this)
        executeNamedValidation("Validate new ServiceCharge", {
            aRate != null ? this.@rate = aRate : issueError(this, [name:"${this.getClass().getSimpleName()}.rate"],
                                                                   "payroll.union.association.rate.required")
        })
    }

    public static DefaultUnionAssociation newUnionAssociation(Employee employee, Integer aRate){
        return new GenericBuilder(DefaultUnionAssociation).withEmployee(employee).withRate(aRate).build()
    }

    @Override
    void postWorkEvent(WorkEvent workEvent) {
        if(workEvent instanceof UnionCharge){
            charges.add(workEvent)
        }
    }

    @Override
    Collection<UnionCharge> getCharges() {
        return charges
    }

    @Override
    Integer getRate() {
        return rate
    }

    @Override
    Employee getEmployee() {
        return employee
    }

    @Override
    Boolean isUnionMember() {
        return true
    }
}
