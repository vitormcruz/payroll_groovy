package com.vmc.payroll.external.config

import com.vmc.concurrency.GeneralUserModel
import com.vmc.payroll.domain.Employee
import com.vmc.payroll.external.persistence.inMemory.repository.CommonInMemoryRepositoryVersion1
import com.vmc.payroll.external.presentation.webservice.spark.EmployeeWebServiceController
import org.apache.http.HttpStatus
import spark.servlet.SparkApplication

import static spark.Spark.*

class PayrollSparkRoutesConfiguration implements SparkApplication {

    EmployeeWebServiceController employeeWebServiceController = new EmployeeWebServiceController(new CommonInMemoryRepositoryVersion1<Employee>(),
                                                                                                 new GeneralUserModel())

    @Override
    void init() {
        path("/api/payroll", {

            before("/*", {req, res -> res.type("application/json") })
            configureEsceptionHandling()
            configureEmployeeRoutes()
        })
    }

    private configureEsceptionHandling() {
        exception(IllegalArgumentException, { exception, request, response ->
            response.type("text/plain")
            response.status(HttpStatus.SC_BAD_REQUEST)
            response.body(exception.message)
        })
    }

    private configureEmployeeRoutes() {
        path("/employee", {

            post("", { req, res ->
                employeeWebServiceController.newEmployee(req, res)
                return res.body()
            })

        })
    }
}
