package com.vmc.payroll.testPreparation

import com.vmc.payroll.config.ServiceLocator


@Singleton(lazy = true, strict = false)
class TestsServiceLocator extends ServiceLocator {

    @Lazy
    DatabaseCleaner databaseCleaner = { new DatabaseCleaner(userModel, employeeRepository) }()

    protected TestsServiceLocator() {
    }
}
