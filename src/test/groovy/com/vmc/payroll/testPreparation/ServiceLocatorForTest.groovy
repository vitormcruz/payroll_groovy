package com.vmc.payroll.testPreparation

import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import com.vmc.payroll.external.config.DatabaseCleaner
import com.vmc.payroll.external.config.ProductionServiceLocator
import com.vmc.payroll.external.config.ServiceLocator
import com.vmc.payroll.external.persistence.orientDB.repository.DummyEntity

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

    @Override
    OObjectDatabaseTx loadDatabase() {
        def database = super.loadDatabase()
        database.getEntityManager().registerEntityClass(DummyEntity)
        return database
    }
}
