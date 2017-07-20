package com.vmc.payroll.external.config

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.vmc.concurrency.api.AtomicBlock
import com.vmc.concurrency.api.ModelSnapshot
import com.vmc.concurrency.singleVM.SingleVMAtomicBlock
import com.vmc.concurrency.singleVM.SingleVMModelSnapshot
import com.vmc.payroll.Employee
import com.vmc.payroll.api.EmployeeRepository
import com.vmc.payroll.api.Repository
import com.vmc.payroll.external.persistence.inMemory.InMemoryEmployeeRepository

class ServiceLocator {

    private static myself = new ServiceLocator()

    private ObjectMapper mapper = new ObjectMapper().configure(MapperFeature.AUTO_DETECT_FIELDS, false)
    AtomicBlock atomicBlock = new SingleVMAtomicBlock()
    private ModelSnapshot modelSnapshot = new SingleVMModelSnapshot(atomicBlock())
    private Repository<Employee> employeeRepository = new InMemoryEmployeeRepository()

    protected ServiceLocator(){
        modelSnapshot.add(employeeRepository)
    }

    static getInstance(){
        return myself
    }

    /**
     * Load another service locator instance
     */
    static load(ServiceLocator serviceLocator){
        myself = serviceLocator
    }

    ObjectMapper mapper() {
        return mapper
    }

    ModelSnapshot modelSnapshot(){
        return modelSnapshot
    }

    AtomicBlock atomicBlock(){
        return atomicBlock
    }

    EmployeeRepository employeeRepository(){
        return employeeRepository
    }
}
