package com.vmc.payroll.testPreparation

import com.vmc.payroll.external.config.DatabaseCleaner
import com.vmc.payroll.external.config.ProductionServiceLocator

class ServiceLocatorForTest extends ProductionServiceLocator{

    @Lazy
    static final ProductionServiceLocator myself = {new ServiceLocatorForTest()}()

    @Lazy
    private DatabaseCleaner databaseCleaner = {new DatabaseCleaner(modelSnapshot, employeeRepository)}()

}
