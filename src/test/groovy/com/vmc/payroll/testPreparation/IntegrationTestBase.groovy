package com.vmc.payroll.testPreparation


import com.vmc.payroll.external.config.DatabaseCleaner
import com.vmc.payroll.external.config.ServiceLocator
import com.vmc.userModel.api.UserModel
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.Before

abstract class IntegrationTestBase extends ValidationNotificationTestSetup{

    protected DatabaseCleaner databaseCleaner = ServiceLocator.instance.databaseCleaner
    protected UserModel model = ServiceLocator.instance.modelSnapshot

    static{
        ServiceLocator.load(ServiceLocatorForTest)
    }

    @Before
    void setUp(){
        super.setUp()
        databaseCleaner.cleanDatabase()
    }

}