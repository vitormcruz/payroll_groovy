package com.vmc.payroll.testPreparation


import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.jupiter.api.BeforeEach

abstract class IntegrationTestBase extends ValidationNotificationTestSetup{

    static TestsServiceLocator serviceLocator =TestsServiceLocator.instance

    @BeforeEach
    void setUp(){
        super.setUp()
        serviceLocator.databaseCleaner.cleanDatabase()
    }

}