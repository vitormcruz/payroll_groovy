package com.vmc.concurrency

import com.vmc.concurrency.api.SyncronizationBlock
import com.vmc.concurrency.api.UserSnapshotListener
import com.vmc.payroll.domain.api.EntityCommonTrait
import com.vmc.payroll.external.config.ServiceLocator
import com.vmc.payroll.testPreparation.ServiceLocatorForTest
import org.junit.Test

class UnitOfWorkUserSnapshotUnitTest {

    public static final int TIMEOUT = 2000

    static {
        ServiceLocator.load(ServiceLocatorForTest)
    }

    @Test
    void "Test obtaining referenced object from user model"(){
        def unitOfWork = new GeneralUserModelSnapshotStub()
        def date = unitOfWork.add(new Date())
        System.gc()
        assertWaitingSuccess({unitOfWork.getUserObjectsSnapshot().contains(date)})
    }

    @Test
    void "Test obtaining unreferenced and unchanged object from user model"(){
        def unitOfWork = new GeneralUserModelSnapshotStub()
        unitOfWork.add(new Date())
        System.gc()
        assertWaitingSuccess({assert unitOfWork.getUserObjectsSnapshot().isEmpty()})
    }

    @Test
    void "Test obtaining unreferenced but changed (dirty) object from user model"(){
        def unitOfWork = new GeneralUserModelSnapshotStub()
        addDateToModelAndChange(unitOfWork, new Date(), {it.setTime(0)})
        System.gc()
        assertWaitingSuccess({ assert unitOfWork.getUserObjectsSnapshot().collect {it.getTime()} == [0] : "Dirty (changed) objects should be retained in the model" })
    }

    @Test
    void "Test saving model with unchanged object"(){
        def unitOfWork = new GeneralUserModelSnapshotStub()
        def savedObjects = new HashSet()
        def date = unitOfWork.add(new Date())
        unitOfWork.save()
        assert savedObjects.isEmpty()
    }

    @Test
    void "Test saving model with changed (dirty) object"(){
        def unitOfWork = new GeneralUserModelSnapshotStub()
        def savedObjects = new HashSet<Long>()
        def date = unitOfWork.add(new Date())
        date.setTime(0)
        unitOfWork.save()
        assert savedObjects == [0L] as Set
    }

    @Test
    void "Test adding the same object multiple times to the model"(){
        def unitOfWork = new GeneralUserModelSnapshotStub()
        def date = new Date()
        def dateReturned1 = unitOfWork.add(date)
        def dateReturned2 = unitOfWork.add(date)
        def dateReturned3 = unitOfWork.add(date)
        assert dateReturned1.subject == dateReturned2.subject && dateReturned2.subject == dateReturned3.subject
        assert dateReturned1.equals(dateReturned2) && dateReturned2.equals(dateReturned3)
    }

    @Test
    void "Test saving a model that had the same object added multiple times to the model with different sincronization closures"(){
        def unitOfWork = new GeneralUserModelSnapshotStub()
        def savedObjects1 = new HashSet<Long>()
        def savedObjects2 = new HashSet<Long>()
        def savedObjects3 = new HashSet<Long>()
        def date = new Date()
        def dateReturned1 = unitOfWork.add(date)
        def dateReturned2 = unitOfWork.add(date)
        def dateReturned3 = unitOfWork.add(date)
        dateReturned3.setTime(0)
        unitOfWork.save()
        assert savedObjects1 == [0L] as Set
        assert savedObjects2 == [0L] as Set
        assert savedObjects3 == [0L] as Set
    }

    @Test
    void "Test saving syncronized execution"(){
        def syncronizationBlockUsed = false
        def unitOfWork = new GeneralUserModelSnapshotStub({syncronizationBlockUsed = true; it()})
        assert !syncronizationBlockUsed
        unitOfWork.save()
        assert syncronizationBlockUsed
    }


    @Test
    void "Test notification of save for one listener"(){
        def unitOfWork = new GeneralUserModelSnapshotStub()
        def notificationCalled = false
        def unitOfWorkListener = { notificationCalled = true } as UserSnapshotListener
        unitOfWork.registerUnitOfWorkerListener(unitOfWorkListener)
        unitOfWork.save()
        assert notificationCalled
    }

    @Test
    void "Test notification of save for N listeners"(){
        def unitOfWork = new GeneralUserModelSnapshotStub()
        def notification1Called = false
        def notification2Called = false
        def unitOfWorkListener1 = { notification1Called = true } as UserSnapshotListener
        def unitOfWorkListener2 = { notification2Called = true } as UserSnapshotListener
        unitOfWork.registerUnitOfWorkerListener(unitOfWorkListener1)
        unitOfWork.registerUnitOfWorkerListener(unitOfWorkListener2)
        unitOfWork.save()
        assert notification1Called && notification2Called
    }

    @Test
    void "Test notification of save for N, but removing some"(){
        def unitOfWork = new GeneralUserModelSnapshotStub()
        def notification1Called = false
        def notification2Called = false
        def unitOfWorkListener1 = { notification1Called = true } as UserSnapshotListener
        def unitOfWorkListener2 = { notification2Called = true } as UserSnapshotListener
        unitOfWork.registerUnitOfWorkerListener(unitOfWorkListener1)
        unitOfWork.registerUnitOfWorkerListener(unitOfWorkListener2)
        unitOfWork.unregisterUnitOfWorkerListener(unitOfWorkListener2)
        unitOfWork.save()
        assert notification1Called && !notification2Called
    }

    @Test
    void "Test unused notification"(){
        def unitOfWork = new GeneralUserModelSnapshotStub()
        unitOfWork.registerUnitOfWorkerListener({ } as UserSnapshotListener)
        System.gc()
        assertWaitingSuccess({assert unitOfWork.getlisteners().isEmpty()})
    }

    @Test
    void "hausaghush"(){
        TesteBla instance = ObjectTracker.createTrackingProxyClassFor(TesteBla).newInstance()

        instance.@subject = new TesteBla(bla: 'aaaa')

        assert instance.metaClass != null
        assert instance.getId() == null
        println(instance.getClass())

        assert instance.bla == 'aaaa'
    }

    static class TesteBla implements EntityCommonTrait{
        String bla

        @Override
        def getId() {
            return null
        }
    }

    void addDateToModelAndChange(GeneralUserModelSnapshotStub unitOfWork, objectToAdd, changeClosure) {
        def date = unitOfWork.add(objectToAdd)
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
    
    static class GeneralUserModelSnapshotStub extends GeneralUserModelSnapshot {

        GeneralUserModelSnapshotStub() {
            super({it()} as SyncronizationBlock)
        }

        GeneralUserModelSnapshotStub(SyncronizationBlock syncronizationBlock) {
            super(syncronizationBlock)
        }

        @Override
        void rollback() {}
    }  
}