package com.vmc.payroll.testPreparation

import com.vmc.payroll.external.config.DatabaseCleaner
import com.vmc.payroll.external.config.DependencyLocator
import com.vmc.userModel.api.UserModel
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.jupiter.api.BeforeEach

abstract class IntegrationTestBase extends ValidationNotificationTestSetup{

    protected DatabaseCleaner databaseCleaner = DependencyLocator.instance.databaseCleaner
    protected UserModel model = DependencyLocator.instance.modelSnapshot

    static{
        DependencyLocator.load(DependencyLocatorForTest)
    }

    @BeforeEach
    void setUp(){
        super.setUp()
        databaseCleaner.cleanDatabase()
    }

}