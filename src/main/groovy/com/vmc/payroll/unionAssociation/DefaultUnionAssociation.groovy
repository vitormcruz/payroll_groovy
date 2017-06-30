package com.vmc.payroll.unionAssociation

import com.vmc.payroll.payment.workEvent.api.UnionCharge
import com.vmc.payroll.payment.workEvent.api.WorkEvent
import com.vmc.payroll.unionAssociation.api.UnionAssociation
import com.vmc.validationNotification.builder.GenericBuilder

import static com.google.common.base.Preconditions.checkArgument
import static com.vmc.validationNotification.ApplicationValidationNotifier.executeNamedValidation

class DefaultUnionAssociation implements UnionAssociation{

    private Integer rate
    private employee
    private charges = []

    protected DefaultUnionAssociation(anEmployee, Integer aRate) {
        checkArgument(anEmployee != null, "An Employee should be provided to a Default Union Association")
        this.employee = anEmployee
        this.employee.registerAsWorkEventHandler(this)
        executeNamedValidation("Validate new ServiceCharge", {
            setRate(aRate)
        })
    }

    static DefaultUnionAssociation newUnionAssociation(employee, Integer aRate){
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
            issueError("payroll.union.association.rate.required", [property:"rate"])
        } else if(newRate < 1){
            issueError("payroll.union.association.rate.mustbe.positive.integer", [property:"rate"])
        }else {
            this.@rate = newRate
        }
    }

    @Override
    def getEmployee() {
        return employee
    }

    @Override
    Boolean isUnionMember() {
        return true
    }
}
