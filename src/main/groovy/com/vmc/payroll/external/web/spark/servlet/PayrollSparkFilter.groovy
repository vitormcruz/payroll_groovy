package com.vmc.payroll.external.web.spark.servlet

import com.vmc.payroll.external.web.spark.common.PayrollSparkRoutesConfiguration
import com.vmc.payroll.external.web.spark.common.SparkRestController
import spark.servlet.SparkApplication
import spark.servlet.SparkFilter

import javax.servlet.FilterConfig
import javax.servlet.ServletException

class PayrollSparkFilter extends SparkFilter {

    Collection<SparkRestController> restControllers

    PayrollSparkFilter(Collection<SparkRestController> restControllers) {
        this.restControllers = restControllers
    }

    @Override
    protected SparkApplication[] getApplications(FilterConfig filterConfig) throws ServletException {
        [new PayrollSparkRoutesConfiguration(restControllers)] as SparkApplication[]
    }
}
