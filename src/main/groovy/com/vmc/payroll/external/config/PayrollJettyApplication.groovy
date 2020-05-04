package com.vmc.payroll.external.config


import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository
import com.vmc.payroll.domain.payment.delivery.Mail
import com.vmc.payroll.domain.payment.type.Monthly
import com.vmc.userModel.api.UserModel
import com.vmc.validationNotification.servlet.ValidationNotifierFilter
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.FilterHolder
import org.eclipse.jetty.webapp.WebAppContext
import spark.servlet.SparkFilter

import javax.servlet.DispatcherType

class PayrollJettyApplication {

    static private FilterHolder configuredSparkFilterHolder

    static private UserModel modelSnapshot
    static private Repository<Employee> employeeRepository

    static {
        String productionserviceLocaleName = System.getProperties().get("production_service_locale_name")
        if(productionserviceLocaleName == null) { productionserviceLocaleName = "com.vmc.payroll.external.config.ProductionServiceLocator" }
        ServiceLocator.load(productionserviceLocaleName as Class)
        configuredSparkFilterHolder = getConfiguredSparkFilterHolder()
        modelSnapshot = ServiceLocator.instance.modelSnapshot
        employeeRepository = ServiceLocator.instance.employeeRepository
    }

    static void main(String[] args) {
        Server server = new Server(7003)

        employeeRepository.add(Employee.newEmployee("Sofia", "Street 1", "sofia@bla.com", {Monthly.newPaymentType(it, 2000)}, {Mail.newPaymentDelivery(it, "Street 1")}))
        employeeRepository.add(Employee.newEmployee("Heloísa", "Street 1", "heloisa@bla.com", {Monthly.newPaymentType(it, 2000)}, {Mail.newPaymentDelivery(it, "Street 1")}))
        modelSnapshot.save()

        def webAppContext = new WebAppContext()
        webAppContext.setContextPath("/sandbox")
        webAppContext.setResourceBase(".")
        webAppContext.setExtractWAR(false)
        webAppContext.setContextPath("/")
        webAppContext.addFilter(ValidationNotifierFilter,"/*", EnumSet.allOf(DispatcherType))
        webAppContext.addFilter(configuredSparkFilterHolder, "/api/payroll/*", EnumSet.allOf(DispatcherType))

        server.setHandler(webAppContext)
        server.start()
        server.join()
    }

    static FilterHolder getConfiguredSparkFilterHolder() {
        def sparkFilter = new SparkFilter()
        def sparkFilterHolder = new FilterHolder(sparkFilter)
        sparkFilterHolder.setInitParameter("applicationClass", "com.vmc.payroll.external.config.PayrollSparkRoutesConfiguration")
        return sparkFilterHolder
    }
}
