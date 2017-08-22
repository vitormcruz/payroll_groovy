package com.vmc.payroll.domain.unionAssociation

import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.payment.attachment.api.UnionCharge
import com.vmc.payroll.domain.payment.attachment.api.WorkDoneProof
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail
import static org.mockito.Mockito.mock

class BasicUnionAssociationUnitTest extends ValidationNotificationTestSetup{

    @Test
    void "Create union association without employee"(){
        def ex = shouldFail {BasicUnionAssociation.newUnionAssociation(null, 10)}
        ex.message == "An Employee should be provided to a Default Union Association"
    }

    @Test
    void "Create union association without rate"(){
        def unionAssociation = BasicUnionAssociation.newUnionAssociation(mock(Employee), null)
        assert validationObserver.errors.contains("The union association rate is required for members")
    }

    @Test
    void "Create union association with rate equals 0"(){
        BasicUnionAssociation.newUnionAssociation(mock(Employee), 0)
        assert validationObserver.errors.contains("The rate must be a positive integer")
    }

    @Test
    void "Create union association negative rate"(){
        BasicUnionAssociation.newUnionAssociation(mock(Employee), -1)
        assert validationObserver.errors.contains("The rate must be a positive integer")
    }

    @Test
    void "Create union association successfully"(){
        def expectedEmployee = mock(Employee)
        def unionAssociation = BasicUnionAssociation.newUnionAssociation(expectedEmployee, 10)
        assert unionAssociation != null
        assert validationObserver.errors.isEmpty() : "${validationObserver.getCommaSeparatedErrors()}"
        assert unionAssociation.employee == expectedEmployee
        assert unionAssociation.rate == 10
    }

    @Test
    void "Adding an Union Charge"(){
        def unionAssociation = BasicUnionAssociation.newUnionAssociation(mock(Employee), 10)
        def unionChargeExpected = [] as UnionCharge
        unionAssociation.postPaymentAttachment(unionChargeExpected)
        assert unionAssociation.getCharges().contains(unionChargeExpected)
    }

    @Test
    void "Adding a non work event attachment"(){
        def unionAssociation = BasicUnionAssociation.newUnionAssociation(mock(Employee), 10)
        def nonUnionCharge = [] as WorkDoneProof
        unionAssociation.postPaymentAttachment(nonUnionCharge)
        assert !unionAssociation.getCharges().contains(nonUnionCharge)
    }

    @Test
    void "Change union association rate to null"(){
        def unionAssociation = BasicUnionAssociation.newUnionAssociation(mock(Employee), 10)
        unionAssociation.setRate(null)
        assert unionAssociation.getRate() == 10
        assert validationObserver.errors.contains("The union association rate is required for members")
    }

    @Test
    void "Change union association rate to 0"(){
        def unionAssociation = BasicUnionAssociation.newUnionAssociation(mock(Employee), 10)
        unionAssociation.setRate(0)
        assert unionAssociation.getRate() == 10
        assert validationObserver.errors.contains("The rate must be a positive integer")
    }

    @Test
    void "Change union association rate to negative value"(){
        def unionAssociation = BasicUnionAssociation.newUnionAssociation(mock(Employee), 10)
        unionAssociation.setRate(-1)
        assert unionAssociation.getRate() == 10
        assert validationObserver.errors.contains("The rate must be a positive integer")
    }

    @Test
    void "Change union association rate to positive value"(){
        def unionAssociation = BasicUnionAssociation.newUnionAssociation(mock(Employee), 10)
        unionAssociation.setRate(5)
        assert unionAssociation.getRate() == 5
        assert validationObserver.errors.isEmpty() : "${validationObserver.getCommaSeparatedErrors()}"
    }

}
