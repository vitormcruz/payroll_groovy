package com.vmc.payroll.testPreparation


import com.vmc.payroll.external.config.DatabaseCleaner
import com.vmc.payroll.external.config.ProductionServiceLocator
import com.vmc.payroll.external.config.ServiceLocator

class ServiceLocatorForTest extends ProductionServiceLocator{

    static myself = new ServiceLocatorForTest()

    static ServiceLocator getInstance(){
        return myself
    }

    @Lazy
    volatile private DatabaseCleaner databaseCleaner = { loadDatabaseCleaner() }()

    DatabaseCleaner loadDatabaseCleaner() {
        new DatabaseCleaner(modelSnapshot, employeeRepository)
    }
}
