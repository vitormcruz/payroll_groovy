package com.vmc.payroll.external.config


import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository
import com.vmc.payroll.external.persistence.inMemory.repository.CommonInMemoryRepositoryVersion2
import com.vmc.userModel.GeneralUserModel
import com.vmc.userModel.UserModelAwareRepository
import com.vmc.userModel.api.UserModel

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
    UserModel loadModelSnapshot() {
        return new GeneralUserModel().with {registerListener(employeeRepository); it}
    }

    @Override
    Repository<Employee> loadEmployeeRepository() {
        return new UserModelAwareRepository<Employee>(new CommonInMemoryRepositoryVersion2<Employee>())
    }

    @Override
    Executor loadExecutor() {
        return {Thread.start(it)} as Executor
    }
}
