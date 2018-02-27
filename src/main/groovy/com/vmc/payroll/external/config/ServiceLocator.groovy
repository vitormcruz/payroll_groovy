package com.vmc.payroll.external.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import com.vmc.concurrency.api.SyncronizationBlock
import com.vmc.concurrency.api.UserModelSnapshot
import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository

import javax.sql.DataSource
import java.util.concurrent.Executor

abstract class ServiceLocator {

    private static currentServiceLocatorClass

    @Lazy
    volatile Properties systemProperties = {getInstance().loadSystemProperties()}()

    @Lazy
    volatile ObjectMapper mapper = {getInstance().loadMapper()}()

    @Lazy
    volatile SyncronizationBlock atomicBlock = {getInstance().loadAtomicBlock()}()

    @Lazy
    volatile UserModelSnapshot modelSnapshot = {getInstance().loadModelSnapshot()}()

    @Lazy
    volatile Repository<Employee> employeeRepository = {getInstance().loadEmployeeRepository()}()

    @Lazy
    volatile OObjectDatabaseTx orientDBDatabase = {loadOrientDBDatabase()}()

    @Lazy
    volatile DataSource dataSource = {getInstance().loadDataSource()}()

    @Lazy
    volatile Executor executor = {getInstance().loadExecutor()}()

    static ServiceLocator getInstance(){
        return currentServiceLocatorClass.getInstance()
    }

    static load(Class serviceLocatorClass){
        currentServiceLocatorClass = serviceLocatorClass
    }

    abstract Properties loadSystemProperties()
    abstract ObjectMapper loadMapper()
    abstract SyncronizationBlock loadAtomicBlock()
    abstract UserModelSnapshot loadModelSnapshot()
    abstract Repository<Employee> loadEmployeeRepository()
    abstract OObjectDatabaseTx loadOrientDBDatabase()
    abstract DataSource loadDataSource()
    abstract Executor loadExecutor()


}
