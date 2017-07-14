package com.vmc.payroll

import com.vmc.payroll.api.EmployeeRepository
import com.vmc.payroll.external.config.ServiceLocator
import com.vmc.payroll.payment.delivery.AccountTransfer
import com.vmc.payroll.payment.delivery.Mail
import com.vmc.payroll.payment.delivery.Paymaster
import com.vmc.payroll.payment.type.Commission
import com.vmc.payroll.payment.type.Hourly
import com.vmc.payroll.payment.type.Monthly
import com.vmc.payroll.payment.workEvent.SalesReceipt
import com.vmc.payroll.payment.workEvent.ServiceCharge
import com.vmc.payroll.payment.workEvent.TimeCard
import com.vmc.payroll.testPreparation.IntegrationTestBase
import com.vmc.validationNotification.builder.DataSetBuilder
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail

class EmployeeIntTest extends IntegrationTestBase {

    private EmployeeRepository employeeRepository = ServiceLocator.instance.employeeRepository()
    private DataSetBuilder employeeBuilder
    private Employee employee1
    private Employee employee2
    private Employee employee3
    private Employee employee4
    private Employee employeeUnion5

    @Before
    void setUp(){
        super.setUp()
        employeeBuilder = new DataSetBuilder(getEmployeeClass(), {
            employeeRepository.add(it)
            model.save()
        })

        employee1 = employeeBuilder.withName("Heloísa").withAddress("Street 1").withEmail("heloisa@bla.com")
                                   .withPaimentType(Monthly, 2000).withPaymentDelivery(Mail, "Street 1").build()
        employee2 = employeeBuilder.withName("Heloísa Medina").withAddress("test address").withEmail("test email")
                                   .withPaimentType(Commission, 2000, 100).withPaymentDelivery(Paymaster).build()
        employee3 = employeeBuilder.withName("Sofia").withAddress("test address").withEmail("test email")
                                   .withPaimentType(Monthly, 2000).withPaymentDelivery(AccountTransfer,  "bank 1", "111111").build()
        employee4 = employeeBuilder.withName("Sofia Medina").withAddress("test address").withEmail("test email")
                                   .withPaimentType(Monthly, 2000).withPaymentDelivery(Mail, "Street 1").build()
        employeeUnion5 = employeeBuilder.withName("Sofia Medina Carvalho").withAddress("test address").withEmail("test email").beUnionMember(5)
                                        .withPaimentType(Hourly, 100).withPaymentDelivery(Mail, "Street 1").build()
    }

    @Test
    void "Get an Employee"(){
        def retrievedEmployee = employeeRepository.get(employee1.id)
        assertMonthlyPaidEmployeeIs(retrievedEmployee, "Heloísa", "Street 1", "heloisa@bla.com", 2000)
    }

    @Test
    void "Add a new monthly paid Employee"(){
        Employee addedEmployee = employeeBuilder.withName("New Employee").withAddress("test adress").withEmail("test email")
                                                .withPaimentType(Monthly, 1000).withPaymentDelivery(Mail, "Street 1").build()
        addedEmployee = employeeRepository.get(addedEmployee.getId())
        assertMonthlyPaidEmployeeIs(addedEmployee, "New Employee", "test adress", "test email", 1000)
    }

    @Test
    void "Add a new hourly paid Employee"(){
        Employee addedEmployee = employeeBuilder.withName("New Employee").withAddress("test adress").withEmail("test email")
                                                .withPaimentType(Hourly, 50).withPaymentDelivery(Mail, "Street 1").build()
        addedEmployee = employeeRepository.get(addedEmployee.getId())
        assertHourlyPaidEmployeeIs(addedEmployee, "New Employee", "test adress", "test email", 50)
    }

    @Test
    void "Edit an Employee"(){
        Employee employeeToChange = employeeRepository.get(employee1.id)
        employeeToChange.name = "Change Test"
        employeeToChange.address = "Change Test adress"
        employeeToChange.email = "Change Test email"
        employeeToChange.bePaid(Monthly, 5000)
        employeeRepository.update(employeeToChange)
        model.save()
        def changedEmployee = employeeRepository.get(employeeToChange.id)
        assertMonthlyPaidEmployeeIs(changedEmployee, "Change Test", "Change Test adress", "Change Test email", 5000)
    }

    @Test
    void "Remove an Employee"(){
        employeeRepository.remove(employee1)
        model.save()
        assert employeeRepository.get(employee1.id) == null
    }

    @Test
    void "Add a new Union member Employee"(){
        def addedEmployee = employeeBuilder.withName("New Employee").withAddress("test adress").withEmail("test email")
                                           .withPaimentType(Monthly, 1000).withPaymentDelivery(Mail, "Street 1").beUnionMember(5).build()
        assertMonthlyPaidEmployeeIs(addedEmployee, "New Employee", "test adress", "test email", 1000)
        assert addedEmployee.isUnionMember() : "Should be an Union Member"
    }

    @Test
    void "Find Employees"(){
        def employeeFound = employeeRepository.findAll {it.name.contains("Medina")}

        assert employeeFound.collect {it.id} as Set == [employee2, employee4, employeeUnion5].collect {it.id} as Set
    }

    @Test
    void "Add a new commission paid Employee"(){
        Employee addedEmployee = employeeBuilder.withName("New Employee").withAddress("test adress").withEmail("test email")
                                                .withPaimentType(Commission, 1000, 20).withPaymentDelivery(Mail, "Street 1").build()
        addedEmployee = employeeRepository.get(addedEmployee.getId())
        assertCommissionPaidEmployeeIs(addedEmployee, "New Employee", "test adress", "test email", 1000, 20)
    }

    @Test
    void "Post a time card"(){
        def expectedDate = new DateTime()
        def expectedTimeCard = TimeCard.newTimeCard(expectedDate, 6)
        employeeUnion5.postWorkEvent(expectedTimeCard)
        employeeRepository.update(employeeUnion5)
        model.save()
        def employeeChanged = employeeRepository.get(employeeUnion5.id)
        assert validationObserver.successful() : "${validationObserver.getCommaSeparatedErrors()}"
        assert employeeChanged.paymentType.getPaymentAttachments().collect{ it.getDate().toString() + "_" + it.getHours()} ==
               [expectedDate.toString() + "_" + 6]
    }

    @Test
    void "Post a sales receipt"(){
        def expectedDate = new DateTime()
        def expectedSalesReceipt = SalesReceipt.newSalesReceipt(expectedDate, 200)
        employee2.postWorkEvent(expectedSalesReceipt)
        employeeRepository.update(employee2)
        model.save()
        def employeeChanged = employeeRepository.get(employee2.id)
        assert validationObserver.successful() : "${validationObserver.getCommaSeparatedErrors()}"
        assert employeeChanged.paymentType.getPaymentAttachments().collect{ it.getDate().toString() + "_" + it.getAmount()} ==
               [expectedDate.toString() + "_" + 200]
    }

    @Test
    void "Post an Union charge"(){
        def expectedDate = new DateTime()
        def expectedServiceCharge = ServiceCharge.newServiceCharge(expectedDate, 5)
        employeeUnion5.postWorkEvent(expectedServiceCharge)
        employeeRepository.update(employeeUnion5)
        model.save()
        def employeeChanged = employeeRepository.get(employeeUnion5.id)
        assert validationObserver.successful() : "${validationObserver.getCommaSeparatedErrors()}"
        assert employeeChanged.unionAssociation.getCharges().collect{ it.getDate().toString() + "_" + it.getAmount()} ==
                [expectedDate.toString() + "_" + 5]
    }

    @Test
    void "Post attachment to monthly paid employee"(){
        def e = shouldFail UnsupportedOperationException,
                           {employee1.postWorkEvent(SalesReceipt.newSalesReceipt(new DateTime(), 200))}

        assert e.getMessage() == "Monthly payment does not have payment attachments"
    }

    private void assertMonthlyPaidEmployeeIs(Employee retrievedEmployee, String expectedEmployeeName,
                                             String expectedEmployeeAddress,
                                             String expectedEmployeeEmail,
                                             Integer expectedSalary) {

        assertBasicEmployeeIs(retrievedEmployee, expectedEmployeeName, expectedEmployeeAddress, expectedEmployeeEmail)
        assert retrievedEmployee.paymentType.salary == expectedSalary
    }

    private void assertHourlyPaidEmployeeIs(Employee retrievedEmployee, String expectedEmployeeName,
                                            String expectedEmployeeAddress,
                                            String expectedEmployeeEmail,
                                            Integer expectedHourRate) {

        assertBasicEmployeeIs(retrievedEmployee, expectedEmployeeName, expectedEmployeeAddress, expectedEmployeeEmail)
        assert retrievedEmployee.paymentType.hourRate == expectedHourRate
    }

    private void assertCommissionPaidEmployeeIs(Employee retrievedEmployee, String expectedEmployeeName,
                                               String expectedEmployeeAddress,
                                               String expectedEmployeeEmail,
                                               Integer expectedSalary, Integer expectedCommissionRate) {

        assertBasicEmployeeIs(retrievedEmployee, expectedEmployeeName, expectedEmployeeAddress, expectedEmployeeEmail)
        assert retrievedEmployee.paymentType.salary == expectedSalary
        assert retrievedEmployee.paymentType.commissionRate == expectedCommissionRate
    }

    private void assertBasicEmployeeIs(Employee retrievedEmployee, String expectedEmployeeName, String expectedEmployeeAddress, String expectedEmployeeEmail) {
        assert retrievedEmployee != null
        assert retrievedEmployee.id != null
        assert retrievedEmployee.name == expectedEmployeeName
        assert retrievedEmployee.address == expectedEmployeeAddress
        assert retrievedEmployee.email == expectedEmployeeEmail
    }

    Class<Employee> getEmployeeClass() {
        return Employee
    }
}
