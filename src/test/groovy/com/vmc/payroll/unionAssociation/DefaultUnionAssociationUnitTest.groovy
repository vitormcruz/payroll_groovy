package com.vmc.payroll.unionAssociation

import com.vmc.payroll.Employee
import com.vmc.payroll.payment.workEvent.api.PaymentAttachment
import com.vmc.payroll.payment.workEvent.api.UnionCharge
import com.vmc.payroll.unionAssociation.imp.DefaultUnionAssociation
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail

class DefaultUnionAssociationUnitTest extends ValidationNotificationTestSetup{

    @Test
    void "Create union association without employee"(){
        def ex = shouldFail {DefaultUnionAssociation.newUnionAssociation(null, 10)}
        ex.message == "An Employee should be provided to a Default Union Association"
    }

    @Test
    void "Create union association without rate"(){
        def unionAssociation = DefaultUnionAssociation.newUnionAssociation([] as Employee, null)
        assert unionAssociation == null
        assert validationObserver.errors.contains("payroll.union.association.rate.required")
    }

    @Test
    void "Create union association successfully"(){
        def expectedEmployee = [] as Employee
        def unionAssociation = DefaultUnionAssociation.newUnionAssociation(expectedEmployee, 10)
        assert unionAssociation != null
        assert validationObserver.errors.isEmpty() : "${validationObserver.getCommaSeparatedErrors()}"
        assert unionAssociation.employee == expectedEmployee
        assert unionAssociation.rate == 10
    }

    @Test
    void "Adding an Union Charge"(){
        def unionAssociation = DefaultUnionAssociation.newUnionAssociation([] as Employee, 10)
        def unionChargeExpected = [] as UnionCharge
        unionAssociation.postWorkEvent(unionChargeExpected)
        assert unionAssociation.getCharges().contains(unionChargeExpected)
    }

    @Test
    void "Adding a non work event attachment"(){
        def unionAssociation = DefaultUnionAssociation.newUnionAssociation([] as Employee, 10)
        def nonUnionCharge = [] as PaymentAttachment
        unionAssociation.postWorkEvent(nonUnionCharge)
        assert !unionAssociation.getCharges().contains(nonUnionCharge)
    }

}
