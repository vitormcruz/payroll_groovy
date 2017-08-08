package com.vmc.payroll.external.persistence.inMemory

import com.vmc.payroll.Employee
import com.vmc.payroll.api.EmployeeRepository
import com.vmc.payroll.external.persistence.inMemory.repository.CommonInMemoryRepository

class InMemoryEmployeeRepository extends CommonInMemoryRepository<Employee> implements EmployeeRepository {


 }
