package com.vmc.payroll.external.config

import com.vmc.payroll.external.presentation.webservice.spark.EmployeeWebServiceController
import com.vmc.payroll.external.presentation.webservice.spark.servlet.PayrollSparkFilter
import com.vmc.userModel.api.UserModel
import com.vmc.validationNotification.servlet.ValidationNotifierFilter
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.FilterHolder
import org.eclipse.jetty.webapp.WebAppContext

import javax.servlet.DispatcherType

class PayrollJettyConfiguration {

    private UserModel userModel
    private EmployeeWebServiceController employeeWebServiceController
    private int port

    PayrollJettyConfiguration(int port, UserModel userModel,
                              EmployeeWebServiceController employeeWebServiceController) {

        this.port = port
        this.userModel = userModel
        this.employeeWebServiceController = employeeWebServiceController
    }

    def startServing(){
        Server server = new Server(port)

        def webAppContext = new WebAppContext()
        webAppContext.setContextPath("/")
        webAppContext.setResourceBase(".")
        webAppContext.setExtractWAR(false)

        ValidationNotifierFilter

        webAppContext.addFilter(new FilterHolder(new ValidationNotifierFilter()), "*", EnumSet.allOf(DispatcherType))
        webAppContext.addFilter(new FilterHolder(new PayrollSparkFilter(employeeWebServiceController)),
                                "/api/*", EnumSet.allOf(DispatcherType))

        server.setHandler(webAppContext)
        server.start()
        server.join()
    }
}
