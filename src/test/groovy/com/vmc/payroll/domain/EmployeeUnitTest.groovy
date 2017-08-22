package com.vmc.payroll.domain

import com.vmc.payroll.domain.payment.delivery.AccountTransfer
import com.vmc.payroll.domain.payment.delivery.Mail
import com.vmc.payroll.domain.payment.type.Commission
import com.vmc.payroll.domain.payment.type.Monthly
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.Before
import org.junit.Test

class EmployeeUnitTest extends ValidationNotificationTestSetup{

    private Employee employeeForChange

    @Before
    void setUp(){
        super.setUp()
        employeeForChange = getEmployeeForChange()
    }

    Employee getEmployeeForChange(){
        return Employee.newEmployee("test name", "test address", "test email", {Monthly.newPaymentType(it, 1000)}, {Mail.newPaymentDelivery(it, "Street 1")})
    }

    @Test
    void "Create employee not providing mandatory information"(){
        Employee employee = Employee.newEmployee(null, null, null, null, null)
        verifyMandatoryErrorsMessagesForCreationWereIssued()
    }

    @Test
    void "Create employee providing mandatory information"(){
        Employee builtEmployee = Employee.newEmployee("test name", "test address", "test email", {Monthly.newPaymentType(it, 1000)}, {Mail.newPaymentDelivery(it, "Street 1")})
        verifyEmployeeWithExpectedData(builtEmployee, "test name", "test address", "test email")
        assert builtEmployee.paymentType.class == Monthly
        assert builtEmployee.paymentType.getSalary() == 1000
    }

    @Test
    void "Change employee not providing mandatory information"(){
        employeeForChange.setName(null)
        employeeForChange.setEmail(null)
        employeeForChange.setAddress(null)
        verifyMandatoryErrorsMessagesForChangingWereIssued()
    }

    @Test
    void "Change employee providing mandatory information"(){
        employeeForChange.setName("test name 2")
        employeeForChange.setEmail("test email 2")
        employeeForChange.setAddress("test address 2")
        employeeForChange.bePaid({Commission.newPaymentType(it, 1000, 100)})
        employeeForChange.receivePaymentBy({AccountTransfer.newPaymentDelivery(it, "bank 1", "111111")})
        verifyEmployeeWithExpectedData(employeeForChange, "test name 2", "test address 2", "test email 2")
        assert employeeForChange.getPaymentType().getClass() == Commission
        assert employeeForChange.getPaymentType().getSalary() == 1000
        assert employeeForChange.getPaymentType().getCommissionRate() == 100
        assert employeeForChange.paymentDelivery.class == AccountTransfer
    }

    @Test
    void "By default, employee should not be member of Union"(){
        assert !getEmployeeForChange().isUnionMember() : "Should not be an union member by default"
    }

    @Test
    void "Validate register Union association"(){
        employeeForChange.beUnionMember(5)
        assert employeeForChange.isUnionMember() : "Should be an union member"
    }

    @Test
    void "Validate de-register Union association"(){
        employeeForChange.beUnionMember(5)
        employeeForChange.dropUnionMembership()
        assert !employeeForChange.isUnionMember() : "Should not be an union member after de-registration"
    }

    private void verifyMandatoryErrorsMessagesForCreationWereIssued() {
        verifyMandatoryErrorsMessagesForChangingWereIssued()
        assert validationObserver.getErrors().contains("The employee payment type is required")
        assert validationObserver.getErrors().contains("The employee payment delivery is required")
    }

    private void verifyMandatoryErrorsMessagesForChangingWereIssued() {
        assert validationObserver.getErrors().contains("The employee name is required")
        assert validationObserver.getErrors().contains("The employee address is required")
        assert validationObserver.getErrors().contains("The employee email is required")
    }


    private void verifyEmployeeWithExpectedData(builtEmployee, String name, String address, String email) {
        assert validationObserver.successful()
        assert builtEmployee.getName() == name
        assert builtEmployee.getAddress() == address
        assert builtEmployee.getEmail() == email
    }
}
