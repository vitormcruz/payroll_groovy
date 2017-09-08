package com.vmc.payroll.external.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.vmc.concurrency.api.AtomicBlock
import com.vmc.concurrency.api.ModelSnapshot
import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository

import javax.sql.DataSource

abstract class ServiceLocator {

    private static currentServiceLocatorClass

    protected final Properties systemProperties
    protected final ObjectMapper mapper
    protected final AtomicBlock atomicBlock
    protected final ModelSnapshot modelSnapshot
    protected final Repository<Employee> employeeRepository
    protected final DataSource dataSource

    static ServiceLocator getInstance(){
        return currentServiceLocatorClass.myself
    }

    static load(Class serviceLocatorClass){
        currentServiceLocatorClass = serviceLocatorClass
    }

}
