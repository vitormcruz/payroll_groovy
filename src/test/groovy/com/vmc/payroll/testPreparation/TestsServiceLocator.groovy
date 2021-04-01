package com.vmc.payroll.testPreparation

import com.vmc.payroll.external.config.DatabaseCleaner
import com.vmc.payroll.external.config.ServiceLocator

@Singleton(lazy = true, strict = false)
class TestsServiceLocator extends ServiceLocator {

    @Lazy
    DatabaseCleaner databaseCleaner = { new DatabaseCleaner(userModel, employeeRepository) }()

    protected TestsServiceLocator() {
    }
}
