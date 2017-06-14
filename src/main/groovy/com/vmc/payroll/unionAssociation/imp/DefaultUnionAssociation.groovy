package com.vmc.payroll.unionAssociation.imp

import com.vmc.payroll.Employee
import com.vmc.payroll.payment.workEvent.api.UnionCharge
import com.vmc.payroll.payment.workEvent.api.WorkEvent
import com.vmc.payroll.unionAssociation.UnionAssociation
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
            setRate(aRate)
        })
    }

    static DefaultUnionAssociation newUnionAssociation(Employee employee, Integer aRate){
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
        return new ArrayList(charges)
    }

    @Override
    Integer getRate() {
        return rate
    }

    @Override
    void setRate(Integer newRate) {
        if(newRate == null){
            issueError(this, [name:"${this.getClass().getSimpleName()}.rate"], "payroll.union.association.rate.required")
        } else if(newRate < 1){
            issueError(this, [name:"${this.getClass().getSimpleName()}.rate"], "payroll.union.association.rate.mustbe.positive.integer")
        }else {
            this.@rate = newRate
        }
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
