package com.vmc.payroll.config

import com.vmc.instantiation.extensions.InstanceCreatedListener
import com.vmc.objectMemento.InMemoryObjectChangeProvider
import com.vmc.objectMemento.ObjectChangeProvider
import com.vmc.payroll.adapter.logback.servlet.EnvVarLoaderLoggerConfiguration
import com.vmc.payroll.adapter.persistence.inMemory.repository.CommonInMemoryRepository
import com.vmc.payroll.adapter.web.spark.EmployeeRestController
import com.vmc.payroll.adapter.web.vaadin.views.MainView
import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository
import com.vmc.userModel.GeneralUserModel
import com.vmc.userModel.UserModelAwareRepository
import com.vmc.userModel.api.UserModel

/**
 * I am a ServiceLocator. Use me to create and link objects reused globally and use me only in main-like classes. I am
 * a singleton, so all objects created here will be reused across the application. <p>
 *
 * <br>
 * <strong>Remember, using a ServiceLocator don't prevent applying DI, so apply it!</strong><p>
 * <br>
 *
 * It is also possible to extend me and create or override properties, that's why I define a protected constructor even
 * if it is not in hold with the strict definition of a singleton. One example of this kind of use is for testing
 * purposes, as following:
 *
 * <p>
 * <br>
 *
 * <pre>
 *  {@code @Singleton}(lazy = true, strict = false)
 *  class TestsServiceLocator extends ServiceLocator {
 *
 *      {@code @Lazy}
 *      EmployeeRestController employeeWebServiceController =
 *          new EmployeeRestControllerForTest(employeeRepository) // Will override employeeWebServiceController
 *                                                                // while reusing employeeRepository property.
 *
 *      {@code @Lazy}
 *      SomeObject someObject = new SomeObject() // Will create the new property called "someObject"
 *
 *      protected TestsServiceLocator() {
 *      }
 *  }
 *
 * </pre>
 *
 */
@Singleton(lazy = true, strict = false)
class ServiceLocator implements InstanceCreatedListener {

    @Lazy
    EnvVarLoaderLoggerConfiguration envVarLoaderLoggerConfiguration = new EnvVarLoaderLoggerConfiguration()

    @Lazy
    UserModel userModel = new GeneralUserModel()

    @Lazy
    ObjectChangeProvider inMemoryObjectChangeProvider = new InMemoryObjectChangeProvider()

    @Lazy
    Repository<Employee> employeeRepository =
            new UserModelAwareRepository<>(new CommonInMemoryRepository<Employee>(), userModel, inMemoryObjectChangeProvider)

    @Lazy
    EmployeeRestController employeeWebServiceController = new EmployeeRestController(employeeRepository)

    /**
     * Vaadin routers associates a URI to a class defining it's View by using the annotation {@code @Router}, and Vaadin
     * takes care of instantiate this. The problem is that I loose control of how a View gets to be created, and so I
     * cannot inject any dependency on it because I do not have access to it's instance. vaadinUIConfiguration defines
     * how my views should be configured, i.e. which beans should be inject through it's properties, and my constructor
     * register myself as a listener of Views to be configured. All my views notify it's creation during it's
     * construction.
     */
    private def vaadinUIConfiguration = [(MainView) : {MainView i -> i.employees = employeeRepository}]

    protected ServiceLocator() {
        this.registerInstanceCreatedListener(MainView)
    }

    @Override
    void instanceCreated(Object newObject) {
        vaadinUIConfiguration.get(newObject.getClass())?.call(newObject)
    }
}
