package com.vmc.payroll.external.config


import com.vmc.payroll.external.presentation.webservice.spark.EmployeeWebServiceController
import org.apache.http.HttpStatus
import spark.servlet.SparkApplication

import static spark.Spark.*

class PayrollSparkRoutesConfiguration implements SparkApplication {

    EmployeeWebServiceController employeeWebServiceController

    PayrollSparkRoutesConfiguration(EmployeeWebServiceController employeeWebServiceController) {
        this.employeeWebServiceController = employeeWebServiceController
    }

    @Override
    void init() {
        path("/api/", {

            before("/*", {req, res -> res.type("application/json") })
            configureExceptionHandling()
            configureEmployeeRoutes()
        })
    }

    private configureExceptionHandling() {
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

        path("/employee", {

            get("", { req, res ->
                employeeWebServiceController.listEmployees(req, res)
                return res.body()
            })

        })
    }
}
