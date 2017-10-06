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
        def date = unitOfWork.addToUserModel(new Date(), { savedObjects.add(it) })
        unitOfWork.save()
        assert savedObjects.isEmpty()
    }

    @Test
    void "Test saving model with changed (dirty) object"(){
        def unitOfWork = new GeneralUnitOfWork()
        def savedObjects = new HashSet<Long>()
        def date = unitOfWork.addToUserModel(new Date(), { savedObjects.add(it.getTime()) })
        date.setTime(0)
        unitOfWork.save()
        assert savedObjects == [0L] as Set
    }

    @Test
    void "Test adding the same object multiple times to the model"(){
        def unitOfWork = new GeneralUnitOfWork()
        def date = new Date()
        def dateReturned1 = unitOfWork.addToUserModel(date, { })
        def dateReturned2 = unitOfWork.addToUserModel(date, { })
        def dateReturned3 = unitOfWork.addToUserModel(date, { })
        assert dateReturned1.proxySubject == dateReturned2.proxySubject && dateReturned2.proxySubject == dateReturned3.proxySubject
        assert dateReturned1.equals(dateReturned2) && dateReturned2.equals(dateReturned3)
    }

    @Test
    void "Test saving amodel that had the same object added multiple times to the model with different sincronization closures"(){
        def unitOfWork = new GeneralUnitOfWork()
        def savedObjects1 = new HashSet<Long>()
        def savedObjects2 = new HashSet<Long>()
        def savedObjects3 = new HashSet<Long>()
        def date = new Date()
        def dateReturned1 = unitOfWork.addToUserModel(date, { savedObjects1.add(it.getTime()) })
        def dateReturned2 = unitOfWork.addToUserModel(date, { savedObjects2.add(it.getTime()) })
        def dateReturned3 = unitOfWork.addToUserModel(date, { savedObjects3.add(it.getTime()) })
        dateReturned3.setTime(0)
        unitOfWork.save()
        assert savedObjects1 == [0L] as Set
        assert savedObjects2 == [0L] as Set
        assert savedObjects3 == [0L] as Set
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
