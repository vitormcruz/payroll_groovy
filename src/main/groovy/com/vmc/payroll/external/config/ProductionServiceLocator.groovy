package com.vmc.payroll.external.config

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.vmc.concurrency.GeneralUserModel
import com.vmc.concurrency.UserSnapshotAwareRepository
import com.vmc.concurrency.api.UserModel
import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository
import com.vmc.payroll.external.persistence.inMemory.repository.CommonInMemoryRepositoryVersion2

import java.util.concurrent.Executor

class ProductionServiceLocator extends ServiceLocator{

    private static myself = new ProductionServiceLocator()

    static ServiceLocator getInstance(){
        return myself
    }

    @Override
    Properties loadSystemProperties() {
        return System.getProperties()
    }

    @Override
    ObjectMapper loadMapper() {
        return new ObjectMapper().configure(MapperFeature.AUTO_DETECT_FIELDS, false)
    }

    @Override
    UserModel loadModelSnapshot() {
        return new GeneralUserModel().with {registerListener(employeeRepository); it}
    }

    @Override
    Repository<Employee> loadEmployeeRepository() {
        return new UserSnapshotAwareRepository<Employee>(new CommonInMemoryRepositoryVersion2<Employee>())
    }

    @Override
    Executor loadExecutor() {
        return {Thread.start(it)} as Executor
    }
}
