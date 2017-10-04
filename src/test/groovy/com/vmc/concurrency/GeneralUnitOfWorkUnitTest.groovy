package com.vmc.concurrency

import com.vmc.payroll.external.config.ServiceLocator
import com.vmc.payroll.testPreparation.ServiceLocatorForTest
import org.junit.Test

class GeneralUnitOfWorkUnitTest {

    public static final int TIMEOUT = 2000

    static {
        ServiceLocator.load(ServiceLocatorForTest)
    }

    @Test
    void "Test obtaining used object from user model"(){
        def unitOfWork = new GeneralUnitOfWork()
        def date = unitOfWork.addToUserModel(new Date())
        System.gc()
        assertWaitingSuccess({unitOfWork.getUserModel().contains(date)})
    }

    @Test
    void "Test obtaining unused and unchanged object from user model"(){
        def unitOfWork = new GeneralUnitOfWork()
        unitOfWork.addToUserModel(new Date())
        System.gc()
        assertWaitingSuccess({assert unitOfWork.getUserModel().isEmpty()})
    }

    def assertWaitingSuccess(assertion, timeout=TIMEOUT, originalError=null){
        try {
            sleep(100)
            timeout -= 100
            assertion()
        } catch (AssertionError e) {
            if(timeout < 0) throw originalError? originalError : e
            assertWaitingSuccess(assertion, timeout, originalError? originalError : e)
        }


    }
}
