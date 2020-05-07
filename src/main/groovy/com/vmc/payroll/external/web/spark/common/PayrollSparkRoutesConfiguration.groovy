package com.vmc.payroll.external.web.spark.common


import org.apache.http.HttpStatus
import spark.servlet.SparkApplication

import static spark.Spark.*

class PayrollSparkRoutesConfiguration implements SparkApplication {

    Collection<SparkRestController> restControllers

    PayrollSparkRoutesConfiguration(Collection<SparkRestController> restControllers) {
        this.restControllers = restControllers
    }

    @Override
    void init() {
        path("/api/", {
//            defaultResponseTransformer(new JsonResponseTransformer())

            before("/*", {req, res -> res.type("application/json") })
            configureExceptionHandling()
            restControllers.each {it.configure()}
        })
    }

    private configureExceptionHandling() {
        exception(IllegalArgumentException, { exception, request, response ->
            response.type("text/plain")
            response.status(HttpStatus.SC_BAD_REQUEST)
            response.body(exception.message)
        })
    }

}
