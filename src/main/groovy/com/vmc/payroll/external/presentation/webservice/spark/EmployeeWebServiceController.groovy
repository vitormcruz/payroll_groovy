package com.vmc.payroll.external.presentation.webservice.spark

import com.vmc.concurrency.api.UserModelSnapshot
import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository
import com.vmc.payroll.external.presentation.converter.EmployeeJsonDTO
import spark.Request
import spark.Response

class EmployeeWebServiceController implements BasicControllerOperationsTrait{

    private Repository<Employee> employeeRepository
    private UserModelSnapshot model

    EmployeeWebServiceController(Repository<Employee> anEmployeeRepository, UserModelSnapshot aModel) {
        this.employeeRepository = anEmployeeRepository
        this.model = aModel
    }

    void newEmployee(Request request, Response response) {
        def listener = getValidationListener()
        Employee employeeBuilder = EmployeeJsonDTO.employeeFromJson(request.body())
        employeeBuilder.onBuildSuccess { newEmployee ->
            employeeRepository.add(newEmployee)
            listener.setBody(newEmployee.asJson())
            model.save()
        }
        listener.fillResponse(response)
    }

    void changeEmployee(Request request, Response response) {
        SparkControllerValidationListener listener = getValidationListener()
        def changedEmployee = getResource(employeeId, employeeRepository)
        changedEmployee.applySetMap(changedAttributes)
        if(listener.successful()){
            employeeRepository.update(changedEmployee)
            listener.setBody(changedEmployee)
            model.save()
        }

        listener.fillResponse(response)
    }

    void deleteEmployee(Request request, Response response) {
        SparkControllerValidationListener listener = getValidationListener()
        Employee employeeSubjectedRemoval = getResource(employeeId, employeeRepository)
        if(listener.successful()) {
            employeeRepository.remove(employeeSubjectedRemoval)
            listener.setBody(employeeSubjectedRemoval)
            model.save()
        }
        listener.fillResponse()
    }

    Collection<Employee> listEmployees() {
        return employeeRepository
    }

}
