package com.vmc.payroll.external.config

import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.payment.delivery.Mail
import com.vmc.payroll.domain.payment.type.Monthly
import com.vmc.payroll.external.web.spark.common.PayrollSparkRoutesConfiguration
import com.vmc.validationNotification.servlet.ValidationNotifierFilter

import javax.servlet.DispatcherType
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener

/* Main class for the Payroll Project */
@WebListener
class PayrollServletContextListener implements ServletContextListener {

    private static final ServiceLocator serviceLocator = ServiceLocator.instance

    @Override
    void contextInitialized(ServletContextEvent sce) {
        println("Loading payroll.")
        def context = sce.getServletContext()
        context.addFilter("Validation Notification", new ValidationNotifierFilter())
               .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType), true, "*")

        context.addFilter("Spark Filter", new PayrollSparkRoutesConfiguration([employeeWebServiceController]))
               .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType), true, "/api/*")

        serviceLocator.envVarLoaderLoggerConfiguration.contextInitialized(sce)
        populateData()
    }

    private void populateData() {
        serviceLocator.employeeRepository.add(Employee.newEmployee("Sofia", "Street 1", "sofia@bla.com",
                                            { Monthly.newPaymentType(it, 2000) },
                                            { Mail.newPaymentDelivery(it, "Street 1") }))

        serviceLocator.employeeRepository.add(Employee.newEmployee("Heloísa", "Street 1", "heloisa@bla.com",
                                            { Monthly.newPaymentType(it, 2000) },
                                            { Mail.newPaymentDelivery(it, "Street 1") }))
        serviceLocator.userModel.save()
    }

    @Override
    void contextDestroyed(ServletContextEvent sce) {
        serviceLocator.envVarLoaderLoggerConfiguration.contextDestroyed(sce)
    }
}
