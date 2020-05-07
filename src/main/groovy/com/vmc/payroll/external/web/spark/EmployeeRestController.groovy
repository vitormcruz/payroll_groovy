package com.vmc.payroll.external.web.spark

import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository
import com.vmc.payroll.external.web.spark.common.BasicControllerOperationsTrait
import com.vmc.payroll.external.web.spark.common.SparkRestController

import static spark.Spark.*

class EmployeeRestController implements BasicControllerOperationsTrait, SparkRestController{

    private Repository<Employee> employeeRepository

    EmployeeRestController(Repository<Employee> anEmployeeRepository) {
        this.employeeRepository = anEmployeeRepository
    }

    @Override
    void configure() {

        path("/employee", {

            post("", r { req, res ->
                Employee employeeBuilder = res.body()
                employeeBuilder.onBuildSuccess { newEmployee ->
                    employeeRepository.add(newEmployee)
                    res.setBody(newEmployee)
                }
            })

            patch(":id", r {req, res  ->
                Employee changedEmployee = getResource(req.params(":id").toLong(), employeeRepository)
                changedEmployee.applySetMap(res.body())
                res.setBody(changedEmployee)
            })

            delete(":id", r {req, res  ->
                Employee employeeSubjectedRemoval = getResource(req.params(":id").toLong(), employeeRepository)
                employeeRepository.remove(employeeSubjectedRemoval)
                res.setBody(employeeSubjectedRemoval)
            })

            get("", r { req, res ->
                res.setBody(new ArrayList(employeeRepository))
            })

        })
    }

}
