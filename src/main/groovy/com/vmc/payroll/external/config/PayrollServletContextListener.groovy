package com.vmc.payroll.external.config

import com.vmc.objectMemento.InMemoryObjectChangeProvider
import com.vmc.objectMemento.ObjectChangeProvider
import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository
import com.vmc.payroll.domain.payment.delivery.Mail
import com.vmc.payroll.domain.payment.type.Monthly
import com.vmc.payroll.external.persistence.inMemory.repository.CommonInMemoryRepository
import com.vmc.payroll.external.web.logbag.servlet.EnvVarLoaderLoggerConfiguration
import com.vmc.payroll.external.web.spark.EmployeeRestController
import com.vmc.payroll.external.web.spark.common.PayrollSparkRoutesConfiguration
import com.vmc.userModel.GeneralUserModel
import com.vmc.userModel.UserModelAwareRepository
import com.vmc.validationNotification.servlet.ValidationNotifierFilter

import javax.servlet.DispatcherType
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener

@WebListener
class PayrollServletContextListener implements ServletContextListener {

    private static final EnvVarLoaderLoggerConfiguration envVarLoaderLoggerConfiguration = new EnvVarLoaderLoggerConfiguration()
    private static final userModel = new GeneralUserModel()
    private static final ObjectChangeProvider inMemoryObjectChangeProvider = new InMemoryObjectChangeProvider()
    private static final Repository<Employee> employeeRepository =
            new UserModelAwareRepository<>(new CommonInMemoryRepository<Employee>(), userModel, inMemoryObjectChangeProvider)

    private static EmployeeRestController employeeWebServiceController =
                        new EmployeeRestController(employeeRepository)

    @Override
    void contextInitialized(ServletContextEvent sce) {
        println("Loading payroll.")
        def context = sce.getServletContext()
        context.addFilter("Validation Notification", new ValidationNotifierFilter())
               .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType), true, "*")

        context.addFilter("Spark Filter", new PayrollSparkRoutesConfiguration([employeeWebServiceController]))
               .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType), true, "/api/*")

        envVarLoaderLoggerConfiguration.contextInitialized(sce)
        populateData()
    }

    private void populateData() {
        employeeRepository.add(Employee.newEmployee("Sofia", "Street 1", "sofia@bla.com",
                { Monthly.newPaymentType(it, 2000) },
                { Mail.newPaymentDelivery(it, "Street 1") }))

        employeeRepository.add(Employee.newEmployee("Heloísa", "Street 1", "heloisa@bla.com",
                { Monthly.newPaymentType(it, 2000) },
                { Mail.newPaymentDelivery(it, "Street 1") }))
        userModel.save()
    }

    @Override
    void contextDestroyed(ServletContextEvent sce) {
        envVarLoaderLoggerConfiguration.contextDestroyed(sce)
    }
}
