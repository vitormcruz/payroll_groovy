package com.vmc.payroll.external.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.vmc.concurrency.api.UserModel
import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository

import java.util.concurrent.Executor

abstract class ServiceLocator {

    private static currentServiceLocatorClass

    @Lazy
    volatile Properties systemProperties = {getInstance().loadSystemProperties()}()

    @Lazy
    volatile ObjectMapper mapper = {getInstance().loadMapper()}()

    @Lazy
    volatile UserModel modelSnapshot = {getInstance().loadModelSnapshot()}()

    @Lazy
    volatile Repository<Employee> employeeRepository = {getInstance().loadEmployeeRepository()}()

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
    abstract UserModel loadModelSnapshot()
    abstract Repository<Employee> loadEmployeeRepository()

    abstract Executor loadExecutor()


}
