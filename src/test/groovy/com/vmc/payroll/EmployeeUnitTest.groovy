package com.vmc.payroll

import com.vmc.payroll.payment.delivery.AccountTransferDelivery
import com.vmc.payroll.payment.delivery.MailDelivery
import com.vmc.payroll.payment.type.Commission
import com.vmc.payroll.payment.type.Monthly
import com.vmc.validationNotification.builder.imp.GenericBuilder
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.Before
import org.junit.Test

class EmployeeUnitTest extends ValidationNotificationTestSetup{

    private Employee employeeForChange

    @Before
    public void setUp(){
        super.setUp()
        employeeForChange = getEmployeeForChange()
    }

    Employee getEmployeeForChange(){
        return new GenericBuilder(getEmployeeClass()).withName("test name")
                                                     .withAddress("test address")
                                                     .withEmail("test email")
                                                     .withPaimentType(Monthly, 1000)
                                                     .withPaymentDelivery(MailDelivery, "Street 1")
                                                     .build()
    }

    @Test
    public void "Create employee not providing mandatory information"(){
        def employee = new GenericBuilder(getEmployeeClass()).withName(null)
                                                             .withAddress(null)
                                                             .withEmail(null)
                                                             .withPaimentType(null)
                                                             .withPaymentDelivery(null)
                                                             .build()
        assert employee == null
        verifyMandatoryErrorsMessagesForCreationWereIssued()
    }

    @Test
    public void "Create employee providing mandatory information"(){
        def EmployeeBuilder = new GenericBuilder(getEmployeeClass())
        Employee builtEmployee = EmployeeBuilder.withName("test name")
                                                .withAddress("test address")
                                                .withEmail("test email")
                                                .withPaimentType(Monthly, 1000)
                                                .withPaymentDelivery(MailDelivery, "Street 1")
                                                .build()

        verifyEmployeeWithExpectedData(builtEmployee, "test name", "test address", "test email")
        assert builtEmployee.paymentType.class == Monthly
        assert builtEmployee.paymentType.getSalary() == 1000
    }

    @Test
    public void "Change employee not providing mandatory information"(){
        employeeForChange.setName(null)
        employeeForChange.setEmail(null)
        employeeForChange.setAddress(null)
        verifyMandatoryErrorsMessagesForChangingWereIssued()
    }

    @Test
    public void "Change employee providing mandatory information"(){
        employeeForChange.setName("test name 2")
        employeeForChange.setEmail("test email 2")
        employeeForChange.setAddress("test address 2")
        employeeForChange.bePaid(Commission, 1000, 100)
        employeeForChange.receivePaymentBy(AccountTransferDelivery, "bank 1", "111111")
        verifyEmployeeWithExpectedData(employeeForChange, "test name 2", "test address 2", "test email 2")
        assert employeeForChange.getPaymentType().getClass() == Commission
        assert employeeForChange.getPaymentType().getSalary() == 1000
        assert employeeForChange.getPaymentType().getCommissionRate() == 100
        assert employeeForChange.paymentDelivery.class == AccountTransferDelivery
    }

    @Test
    public void "By default, employee should not be member of Union"(){
        assert !getEmployeeForChange().isUnionMember() : "Should not be an union member by default"
    }

    @Test
    public void "Validate register Union association"(){
        employeeForChange.beUnionMember(5)
        assert employeeForChange.isUnionMember() : "Should be an union member"
    }

    @Test
    public void "Validate de-register Union association"(){
        employeeForChange.beUnionMember(5)
        employeeForChange.dropUnionMembership()
        assert !employeeForChange.isUnionMember() : "Should not be an union member after de-registration"
    }

    private void verifyMandatoryErrorsMessagesForCreationWereIssued() {
        verifyMandatoryErrorsMessagesForChangingWereIssued()
        assert validationObserver.getErrors().contains("payroll.employee.payment.type.mandatory")
        assert validationObserver.getErrors().contains("payroll.employee.payment.delivery.mandatory")
    }

    private void verifyMandatoryErrorsMessagesForChangingWereIssued() {
        assert validationObserver.getErrors().contains("payroll.employee.name.mandatory")
        assert validationObserver.getErrors().contains("payroll.employee.address.mandatory")
        assert validationObserver.getErrors().contains("payroll.employee.email.mandatory")
    }


    private void verifyEmployeeWithExpectedData(builtEmployee, String name, String address, String email) {
        assert validationObserver.successful()
        assert builtEmployee.getName() == name
        assert builtEmployee.getAddress() == address
        assert builtEmployee.getEmail() == email
    }

    Class<Employee> getEmployeeClass() {
        return Employee
    }
}
