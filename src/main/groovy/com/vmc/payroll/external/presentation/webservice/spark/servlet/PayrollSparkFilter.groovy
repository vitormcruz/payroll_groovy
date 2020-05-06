package com.vmc.payroll.external.presentation.webservice.spark.servlet

import com.vmc.payroll.external.config.PayrollSparkRoutesConfiguration
import com.vmc.payroll.external.presentation.webservice.spark.EmployeeWebServiceController
import spark.servlet.SparkApplication
import spark.servlet.SparkFilter

import javax.servlet.FilterConfig
import javax.servlet.ServletException

class PayrollSparkFilter extends SparkFilter {

    EmployeeWebServiceController employeeWebServiceController

    PayrollSparkFilter(EmployeeWebServiceController employeeWebServiceController) {
        this.employeeWebServiceController = employeeWebServiceController
    }

    @Override
    protected SparkApplication[] getApplications(FilterConfig filterConfig) throws ServletException {
        [new PayrollSparkRoutesConfiguration(employeeWebServiceController)] as SparkApplication[]
    }
}
