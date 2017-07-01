package com.vmc.payroll.external.config

import com.vaadin.server.VaadinServlet
import com.vmc.concurrency.ModelSnapshot
import com.vmc.payroll.Employee
import com.vmc.payroll.api.EmployeeRepository
import com.vmc.payroll.payment.delivery.Mail
import com.vmc.payroll.payment.type.Monthly
import com.vmc.validationNotification.builder.imp.GenericBuilder
import com.vmc.validationNotification.servlet.ValidationNotifierFilter
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.FilterHolder
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.webapp.WebAppContext
import spark.servlet.SparkFilter

import javax.servlet.DispatcherType

class PayrollJettyApplication {

    static private ServletHolder configuredVaadinServletHolder
    static private FilterHolder configuredSparkFilterHolder

    static private ModelSnapshot modelSnapshot = ServiceLocator.instance.modelSnapshot()
    static private EmployeeRepository employeeRepository = ServiceLocator.instance.employeeRepository()

    static {
        configuredVaadinServletHolder = getConfiguredVaadinServletHolder()
        configuredSparkFilterHolder = getConfiguredSparkFilterHolder()
    }

    static void main(String[] args) {
        Server server = new Server(7003)


        employeeRepository.add(new GenericBuilder(Employee).withName("Sofia").withAddress("Street 1").withEmail("sofia@bla.com")
                                    .withPaimentType(Monthly, 2000).withPaymentDelivery(Mail, "Street 1").build())

        employeeRepository.add(new GenericBuilder(Employee).withName("Heloísa").withAddress("Street 1").withEmail("heloisa@bla.com")
                                    .withPaimentType(Monthly, 2000).withPaymentDelivery(Mail, "Street 1").build())

        modelSnapshot.save()

        def webAppContext = new WebAppContext()
        webAppContext.setContextPath("/sandbox")
        webAppContext.setResourceBase(".")
        webAppContext.setExtractWAR(false)
        webAppContext.setContextPath("/")
        webAppContext.addServlet(configuredVaadinServletHolder, "/VAADIN/*")
        webAppContext.addServlet(configuredVaadinServletHolder, "/payroll/*")
        webAppContext.addFilter(ValidationNotifierFilter,"/*", EnumSet.allOf(DispatcherType))
        webAppContext.addFilter(configuredSparkFilterHolder, "/api/payroll/*", EnumSet.allOf(DispatcherType))

        server.setHandler(webAppContext)
        server.start()
        server.join()
    }

    static ServletHolder getConfiguredVaadinServletHolder() {
        def vaadinServlet = new VaadinServlet()
        def servletVaadinHolder = new ServletHolder(vaadinServlet)
        servletVaadinHolder.setInitParameter("productionMode", "false")
        servletVaadinHolder.setInitParameter("UI", "com.vmc.payroll.external.presentation.vaadin.view.PayrollUI")
        servletVaadinHolder.setInitParameter("async-supported", "true")
        servletVaadinHolder.setInitParameter("org.atmosphere.useWebSocketAndServlet3", "true")
        return servletVaadinHolder
    }

    static FilterHolder getConfiguredSparkFilterHolder() {
        def sparkFilter = new SparkFilter()
        def sparkFilterHolder = new FilterHolder(sparkFilter)
        sparkFilterHolder.setInitParameter("applicationClass", "com.vmc.payroll.external.config.PayrollSparkRoutesConfiguration")
        return sparkFilterHolder
    }
}
