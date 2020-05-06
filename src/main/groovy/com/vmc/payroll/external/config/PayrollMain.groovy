package com.vmc.payroll.external.config

import com.vmc.objectMemento.InMemoryObjectChangeProvider
import com.vmc.objectMemento.ObjectChangeProvider
import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository
import com.vmc.payroll.domain.payment.delivery.Mail
import com.vmc.payroll.domain.payment.type.Monthly
import com.vmc.payroll.external.persistence.inMemory.repository.CommonInMemoryRepositoryVersion2
import com.vmc.payroll.external.presentation.webservice.spark.EmployeeWebServiceController
import com.vmc.userModel.GeneralUserModel
import com.vmc.userModel.UserModelAwareRepository

class PayrollMain {

    private static userModel = new GeneralUserModel()
    private static ObjectChangeProvider inMemoryObjectChangeProvider = new InMemoryObjectChangeProvider()
    private static Repository<Employee> employeeRepository =
                new UserModelAwareRepository<>(new CommonInMemoryRepositoryVersion2<Employee>(), userModel,
                                               inMemoryObjectChangeProvider)

    private static EmployeeWebServiceController employeeWebServiceController =
                        new EmployeeWebServiceController(employeeRepository)

    private static PayrollJettyConfiguration payrollJettyConfiguration =
                        new PayrollJettyConfiguration(7003, userModel, employeeWebServiceController)


    static void main(String[] args) {
        employeeRepository.add(Employee.newEmployee("Sofia", "Street 1", "sofia@bla.com",
                                                    { Monthly.newPaymentType(it, 2000)},
                                                    { Mail.newPaymentDelivery(it, "Street 1")}))

        employeeRepository.add(Employee.newEmployee("Heloísa", "Street 1", "heloisa@bla.com",
                                                    {Monthly.newPaymentType(it, 2000)},
                                                    {Mail.newPaymentDelivery(it, "Street 1")}))
        userModel.save()

        payrollJettyConfiguration.startServing()

    }
}
