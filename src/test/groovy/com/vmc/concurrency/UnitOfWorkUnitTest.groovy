package com.vmc.concurrency

import com.vmc.payroll.external.config.ServiceLocator
import com.vmc.payroll.testPreparation.ServiceLocatorForTest
import org.junit.Test

class UnitOfWorkUnitTest {

    public static final int TIMEOUT = 2000

    static {
        ServiceLocator.load(ServiceLocatorForTest)
    }

    @Test
    void "Test obtaining used object from user model"(){
        def unitOfWork = new GeneralUnitOfWork()
        def date = unitOfWork.addToUserModel(new Date(), {})
        System.gc()
        assertWaitingSuccess({unitOfWork.getUserModel().contains(date)})
    }

    @Test
    void "Test obtaining unused and unchanged object from user model"(){
        def unitOfWork = new GeneralUnitOfWork()
        unitOfWork.addToUserModel(new Date(), {})
        System.gc()
        assertWaitingSuccess({assert unitOfWork.getUserModel().isEmpty()})
    }

    @Test
    void "Test obtaining unused but changed (dirty) object from user model"(){
        def unitOfWork = new GeneralUnitOfWork()
        addDateToModelAndChange(unitOfWork, new Date(), {it.setTime(0)}, {})
        System.gc()
        assertWaitingSuccess({ assert unitOfWork.getUserModel().collect {it.getTime()} == [0] : "Dirty (changed) objects should be retained in the model" })
    }

    @Test
    void "Test saving model with unchanged object"(){
        def unitOfWork = new GeneralUnitOfWork()
        def savedObjects = new HashSet()
        def date = new Date()
        unitOfWork.addToUserModel(date, { savedObjects.add(it) })
        unitOfWork.save()
        assert savedObjects.isEmpty()
    }

    @Test
    void "Test saving model with changed (dirty) object"(){
        def unitOfWork = new GeneralUnitOfWork()
        def savedObjects = new HashSet()
        def date = new Date()
        unitOfWork.addToUserModel(date, { savedObjects.add(it) })
        date.setTime(0)
        unitOfWork.save()
        assert savedObjects.collect {it.getTime()} as Set == [0] as Set
    }

    public void addDateToModelAndChange(GeneralUnitOfWork unitOfWork, objectToAdd, changeClosure, syncronizeObjectClosure) {
        def date = unitOfWork.addToUserModel(objectToAdd, syncronizeObjectClosure)
        changeClosure(date)
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
