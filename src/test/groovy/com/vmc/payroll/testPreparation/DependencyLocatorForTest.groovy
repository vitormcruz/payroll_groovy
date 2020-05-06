package com.vmc.payroll.testPreparation

import com.vmc.payroll.external.config.DatabaseCleaner
import com.vmc.payroll.external.config.DependencyLocator
import com.vmc.payroll.external.config.PayrollDependencyLocator

class DependencyLocatorForTest extends PayrollDependencyLocator{

    static myself = new DependencyLocatorForTest()

    static DependencyLocator getInstance(){
        return myself
    }

    @Lazy
    volatile private DatabaseCleaner databaseCleaner = { loadDatabaseCleaner() }()

    DatabaseCleaner loadDatabaseCleaner() {
        new DatabaseCleaner(modelSnapshot, employeeRepository)
    }
}
