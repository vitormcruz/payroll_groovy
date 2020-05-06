package com.vmc.payroll.external.config

import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository
import com.vmc.userModel.api.UserModel

abstract class DependencyLocator {

    private static currentServiceLocatorClass

    @Lazy
    volatile UserModel modelSnapshot = { getInstance().loadModelSnapshot() }()

    @Lazy
    volatile Repository<Employee> employeeRepository = { getInstance().loadEmployeeRepository() }()

    static DependencyLocator getInstance(){
        return currentServiceLocatorClass.getInstance()
    }

    static load(Class serviceLocatorClass){
        currentServiceLocatorClass = serviceLocatorClass
    }

    abstract UserModel loadModelSnapshot()
    abstract Repository<Employee> loadEmployeeRepository()

}
