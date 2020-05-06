package com.vmc.payroll.external.config

import com.vmc.objectMemento.InMemoryObjectChangeProvider
import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository
import com.vmc.payroll.external.persistence.inMemory.repository.CommonInMemoryRepositoryVersion2
import com.vmc.userModel.GeneralUserModel
import com.vmc.userModel.UserModelAwareRepository
import com.vmc.userModel.api.UserModel

class PayrollDependencyLocator extends DependencyLocator{

    private static myself = new PayrollDependencyLocator()

    static DependencyLocator getInstance(){
        return myself
    }

    @Override
    UserModel loadModelSnapshot() {
        return new GeneralUserModel()
    }

    @Override
    Repository<Employee> loadEmployeeRepository() {
        return new UserModelAwareRepository<Employee>(new CommonInMemoryRepositoryVersion2<Employee>(), modelSnapshot, new InMemoryObjectChangeProvider())
    }

}
