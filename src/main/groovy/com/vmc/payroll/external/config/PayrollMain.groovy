package com.vmc.payroll.external.config

import com.vmc.objectMemento.InMemoryObjectChangeProvider
import com.vmc.objectMemento.ObjectChangeProvider
import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository
import com.vmc.payroll.domain.payment.delivery.Mail
import com.vmc.payroll.domain.payment.type.Monthly
import com.vmc.payroll.external.persistence.inMemory.repository.CommonInMemoryRepository
import com.vmc.payroll.external.web.spark.EmployeeRestController
import com.vmc.userModel.GeneralUserModel
import com.vmc.userModel.UserModelAwareRepository

class PayrollMain {

    private static userModel = new GeneralUserModel()
    private static ObjectChangeProvider inMemoryObjectChangeProvider = new InMemoryObjectChangeProvider()
    private static Repository<Employee> employeeRepository =
                new UserModelAwareRepository<>(new CommonInMemoryRepository<Employee>(), userModel,
                                               inMemoryObjectChangeProvider)

    private static EmployeeRestController employeeWebServiceController =
                        new EmployeeRestController(employeeRepository)

    static void main(String[] args) {
        employeeRepository.add(Employee.newEmployee("Sofia", "Street 1", "sofia@bla.com",
                                                    { Monthly.newPaymentType(it, 2000)},
                                                    { Mail.newPaymentDelivery(it, "Street 1")}))

        employeeRepository.add(Employee.newEmployee("Heloísa", "Street 1", "heloisa@bla.com",
                                                    {Monthly.newPaymentType(it, 2000)},
                                                    {Mail.newPaymentDelivery(it, "Street 1")}))
        userModel.save()

    }
}
