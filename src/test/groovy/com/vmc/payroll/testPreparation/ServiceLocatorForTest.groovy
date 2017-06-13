package com.vmc.payroll.testPreparation

import com.vmc.payroll.external.config.DatabaseCleaner
import com.vmc.payroll.external.config.ServiceLocator

class ServiceLocatorForTest extends ServiceLocator{
    private DatabaseCleaner databaseCleaner = new DatabaseCleaner(modelSnapshot(), employeeRepository())

    ServiceLocatorForTest() {
    }

    DatabaseCleaner databaseCleaner() {
        return databaseCleaner
    }
}