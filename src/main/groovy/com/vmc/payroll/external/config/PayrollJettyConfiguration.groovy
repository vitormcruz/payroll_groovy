package com.vmc.payroll.external.config


import com.vmc.payroll.external.web.spark.common.SparkRestController
import com.vmc.payroll.external.web.spark.servlet.PayrollSparkFilter
import com.vmc.userModel.api.UserModel
import com.vmc.validationNotification.servlet.ValidationNotifierFilter
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.FilterHolder
import org.eclipse.jetty.webapp.WebAppContext

import javax.servlet.DispatcherType

class PayrollJettyConfiguration {

    private UserModel userModel
    private Collection<SparkRestController> restControllers
    private int port

    PayrollJettyConfiguration(int port, UserModel userModel,
                              Collection<SparkRestController> restControllers) {

        this.port = port
        this.userModel = userModel
        this.restControllers = restControllers
    }

    def startServing(){
        Server server = new Server(port)

        def webAppContext = new WebAppContext()
        webAppContext.setContextPath("/")
        webAppContext.setResourceBase(".")
        webAppContext.setExtractWAR(false)

        webAppContext.addFilter(new FilterHolder(new ValidationNotifierFilter()), "*", EnumSet.allOf(DispatcherType))
        webAppContext.addFilter(new FilterHolder(new PayrollSparkFilter(restControllers)),
                                "/api/*", EnumSet.allOf(DispatcherType))

        server.setHandler(webAppContext)
        server.start()
        server.join()
    }
}
