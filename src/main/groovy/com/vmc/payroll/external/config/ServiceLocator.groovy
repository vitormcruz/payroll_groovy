package com.vmc.payroll.external.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import com.vmc.concurrency.api.AtomicBlock
import com.vmc.concurrency.api.ModelSnapshot
import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository

import javax.sql.DataSource

abstract class ServiceLocator {

    private static currentServiceLocatorClass

    @Lazy
    volatile Properties systemProperties = {getInstance().loadSystemProperties()}()

    @Lazy
    volatile ObjectMapper mapper = {getInstance().loadMapper()}()

    @Lazy
    volatile AtomicBlock atomicBlock = {getInstance().loadAtomicBlock()}()

    @Lazy
    volatile ModelSnapshot modelSnapshot = {getInstance().loadModelSnapshot()}()

    @Lazy
    volatile Repository<Employee> employeeRepository = {getInstance().loadEmployeeRepository()}()

    @Lazy
    volatile OObjectDatabaseTx database = {loadDatabase()}()

    @Lazy
    volatile DataSource dataSource = {getInstance().loadDataSource()}()

    static ServiceLocator getInstance(){
        return currentServiceLocatorClass.getInstance()
    }

    static load(Class serviceLocatorClass){
        currentServiceLocatorClass = serviceLocatorClass
    }

    abstract Properties loadSystemProperties()
    abstract ObjectMapper loadMapper()
    abstract AtomicBlock loadAtomicBlock()
    abstract ModelSnapshot loadModelSnapshot()
    abstract Repository<Employee> loadEmployeeRepository()
    abstract OObjectDatabaseTx loadDatabase()
    abstract DataSource loadDataSource()


}
