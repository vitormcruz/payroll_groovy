package com.vmc.payroll.domain

import com.vmc.objectMother.ObjectMother
import com.vmc.payroll.domain.api.Repository
import com.vmc.payroll.domain.payment.attachment.SalesReceipt
import com.vmc.payroll.domain.payment.attachment.ServiceCharge
import com.vmc.payroll.domain.payment.attachment.TimeCard
import com.vmc.payroll.domain.payment.delivery.AccountTransfer
import com.vmc.payroll.domain.payment.delivery.Mail
import com.vmc.payroll.domain.payment.delivery.Paymaster
import com.vmc.payroll.domain.payment.type.Commission
import com.vmc.payroll.domain.payment.type.Hourly
import com.vmc.payroll.domain.payment.type.Monthly
import com.vmc.payroll.external.config.ServiceLocator
import com.vmc.payroll.testPreparation.IntegrationTestBase
import com.vmc.userModel.GeneralUserModel
import com.vmc.userModel.api.UserModel
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDateTime

import static groovy.test.GroovyAssert.shouldFail

class EmployeeIntTest extends IntegrationTestBase {

    private Repository<Employee> employeeRepository = ServiceLocator.instance.employeeRepository
    private ObjectMother<Employee> employeeMother
    private Employee employee1
    private Employee employee2
    private Employee employee3
    private Employee employee4
    private Employee employee5

    @BeforeAll
    static void setUpAll(){
        def userModelSnapshot = new GeneralUserModel()
        UserModel.load(userModelSnapshot)
    }

    @BeforeEach
    void setUp(){
        super.setUp()
        employeeMother = new ObjectMother<Employee>(getEmployeeClass()).configurePostBirthScript({
            employeeRepository.add(it)
            model.save()
        })

        employee1 = employeeMother.createNewBornWithScript { setName("Heloísa"); setAddress("Street 1"); setEmail("heloisa@bla.com")
                                                             bePaid {Monthly.newPaymentType(it, 2000)}
                                                             receivePaymentBy {Mail.newPaymentDelivery(it, "Street 1")} }

        employee2 = employeeMother.createNewBornWithScript { setName("Heloísa Medina"); setAddress("test address"); setEmail("test email")
                                                             bePaid {Commission.newPaymentType(it,2000, 100)}
                                                             receivePaymentBy {Paymaster.newPaymentDelivery(it)}}

        employee3 = employeeMother.createNewBornWithScript{ setName("Sofia"); setAddress("test address"); setEmail("test email")
                                                            bePaid {Monthly.newPaymentType(it, 2000)}
                                                            receivePaymentBy {AccountTransfer.newPaymentDelivery(it,"bank 1", "111111")}}

        employee4 = employeeMother.createNewBornWithScript{ setName("Sofia Medina"); setAddress("test address"); setEmail("test email")
                                                            bePaid {Monthly.newPaymentType(it, 2000)}
                                                            receivePaymentBy {Mail.newPaymentDelivery(it,"Street 1")} }


        employee5 = employeeMother.createNewBornWithScript { setName("Sofia Medina Carvalho"); setAddress("test address"); setEmail("test email")
                                                             bePaid {Hourly.newPaymentType(it, 100)}
                                                             receivePaymentBy {Mail.newPaymentDelivery(it,"Street 1")}
                                                             beUnionMember(5) }
    }

    @Test
    void "Get an Employee"(){
        def retrievedEmployee = employeeRepository.get(employee1.id)
        assertMonthlyPaidEmployeeIs(retrievedEmployee, "Heloísa", "Street 1", "heloisa@bla.com", 2000)
    }

    @Test
    void "Add a new monthly paid Employee"(){
        Employee addedEmployee = employeeMother.createNewBornWithScript{ setName("New Employee"); setAddress("test adress"); setEmail("test email")
                                                                         bePaid {Monthly.newPaymentType(it,1000)}
                                                                         receivePaymentBy {Mail.newPaymentDelivery(it, "Street 1")} }

        addedEmployee = employeeRepository.get(addedEmployee.getId())
        assertMonthlyPaidEmployeeIs(addedEmployee, "New Employee", "test adress", "test email", 1000)
    }

    @Test
    void "Add a new hourly paid Employee"(){
        Employee addedEmployee  = employeeMother.createNewBornWithScript { setName("New Employee"); setAddress("test adress"); setEmail("test email")
                                                                           bePaid {Hourly.newPaymentType(it,50)}
                                                                           receivePaymentBy {Mail.newPaymentDelivery(it, "Street 1")} }

        assertHourlyPaidEmployeeIs(addedEmployee, "New Employee", "test adress", "test email", 50)
    }

    @Test
    void "Edit an Employee"(){
        Employee employeeToChange = employeeRepository.get(employee1.id)
        employeeToChange.name = "Change Test"
        employeeToChange.address = "Change Test adress"
        employeeToChange.email = "Change Test email"
        employeeToChange.bePaid({Monthly.newPaymentType(it,5000)})
        employeeRepository.update(employeeToChange)
        model.save()
        def changedEmployee = employeeRepository.get(employeeToChange.id)
        assertMonthlyPaidEmployeeIs(changedEmployee, "Change Test",
                                    "Change Test adress", "Change Test email", 5000)
    }

    @Test
    void "Remove an Employee"(){
        employeeRepository.remove(employee1)
        model.save()
        assert employeeRepository.get(employee1.id) == null
    }

    @Test
    void "Add a new Union member Employee"(){
        def addedEmployee = employeeMother.createNewBornWithScript { setName("New Employee"); setAddress("test adress"); setEmail("test email")
                                                                     bePaid {Monthly.newPaymentType(it,1000)}
                                                                     receivePaymentBy {Mail.newPaymentDelivery(it,"Street 1")}
                                                                     beUnionMember(5) }

        assertMonthlyPaidEmployeeIs(addedEmployee, "New Employee", "test adress", "test email", 1000)
        assert addedEmployee.isUnionMember() : "Should be an Union Member"
    }

    @Test
    void "Find Employees"(){
        def employeeFound = employeeRepository.findAll {it.name.contains("Medina")}

        assert employeeFound.collect {it.id} as Set == [employee2, employee4, employee5].collect {it.id} as Set
    }

    @Test
    void "Add a new commission paid Employee"(){
        Employee addedEmployee = employeeMother.createNewBornWithScript{ setName("New Employee"); setAddress("test adress"); setEmail("test email")
                                                                         bePaid{Commission.newPaymentType(it, 1000, 20)}
                                                                         receivePaymentBy{Mail.newPaymentDelivery(it, "Street 1")}}

        assertCommissionPaidEmployeeIs(addedEmployee, "New Employee", "test adress", "test email", 1000, 20)
    }

    @Test
    void "Post a time card"(){
        def expectedDate = LocalDateTime.now()
        def expectedTimeCard = TimeCard.newTimeCard(expectedDate, 6)
        employee5.postPaymentAttachment(expectedTimeCard)
        employeeRepository.update(employee5)
        model.save()
        def employeeChanged = employeeRepository.get(employee5.id)
        assert validationObserver.successful() : "${validationObserver.getCommaSeparatedErrors()}"
        assert employeeChanged.paymentType.getPaymentAttachments().collect{ it.getDate().toString() + "_" + it.getHours()} ==
               [expectedDate.toString() + "_" + 6]
    }

    @Test
    void "Post a sales receipt"(){
        def expectedDate = LocalDateTime.now()
        def expectedSalesReceipt = SalesReceipt.newSalesReceipt(expectedDate, 200)
        employee2.postPaymentAttachment(expectedSalesReceipt)
        employeeRepository.update(employee2)
        model.save()
        def employeeChanged = employeeRepository.get(employee2.id)
        assert validationObserver.successful() : "${validationObserver.getCommaSeparatedErrors()}"
        assert employeeChanged.paymentType.getPaymentAttachments().collect{ it.getDate().toString() + "_" + it.getAmount()} ==
               [expectedDate.toString() + "_" + 200]
    }

    @Test
    void "Post an Union charge"(){
        def expectedDate = LocalDateTime.now()
        def expectedServiceCharge = ServiceCharge.newServiceCharge(expectedDate, 5)
        employee5.postPaymentAttachment(expectedServiceCharge)
        employeeRepository.update(employee5)
        model.save()
        def employeeChanged = employeeRepository.get(employee5.id)
        assert validationObserver.successful() : "${validationObserver.getCommaSeparatedErrors()}"
        assert employeeChanged.unionAssociation.getCharges().collect{ it.getDate().toString() + "_" + it.getAmount()} ==
                [expectedDate.toString() + "_" + 5]
    }

    @Test
    void "Post attachment to monthly paid employee"(){
        def e = shouldFail UnsupportedOperationException,
                           {employee1.postPaymentAttachment(SalesReceipt.newSalesReceipt(LocalDateTime.now(), 200))}

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
