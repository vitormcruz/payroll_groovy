package com.vmc.concurrency

import com.vmc.concurrency.api.ObjectChangeProvider
import com.vmc.concurrency.api.SynchronizationBlock
import com.vmc.concurrency.api.UserSnapshotListener
import com.vmc.payroll.external.config.ServiceLocator
import com.vmc.payroll.testPreparation.ServiceLocatorForTest
import org.junit.Test

import static org.mockito.Mockito.mock


//TODO adjust all naming to UserModel, i think it is better for it to have a similar behavior of a memento.
class UserModelSnapshotUnitTest {

    public static final int TIMEOUT = 2000

    static {
        ServiceLocator.load(ServiceLocatorForTest)
    }

    @Test
    void "Test obtaining referenced object from user model"(){
        def modelSnapshot = new GeneralUserModelSnapshotStub()
        def date = modelSnapshot.manageObject(new Date(), mock(ObjectChangeProvider))
        System.gc()
        assertWaitingSuccess({modelSnapshot.getManagedObjects().contains(date)})
    }

    @Test
    void "Test obtaining unreferenced and unchanged object from user model"(){
        def modelSnapshot = new GeneralUserModelSnapshotStub()
        modelSnapshot.manageObject(new Date(), mock(ObjectChangeProvider))
        System.gc()
        assertWaitingSuccess({assert modelSnapshot.getManagedObjects().isEmpty()})
    }

    @Test
    void "Test obtaining unreferenced but changed (dirty) object from user model"(){
        def modelSnapshot = new GeneralUserModelSnapshotStub()
        addDateToModelAndChange(modelSnapshot, new Date(), {it.setTime(0)})
        System.gc()
        assertWaitingSuccess({
            assert !modelSnapshot.getManagedObjects().isEmpty() &&
                    modelSnapshot.getManagedObjects().first().getObject().getTime() == 0 :
                    "Dirty (changed) objects should be retained in the model"
        })
    }

    @Test
    void "Test saving model with unchanged object"(){
        def modelSnapshot = new GeneralUserModelSnapshotStub()
        def savedObjects = new HashSet()
        def date = modelSnapshot.manageObject(new Date(), mock(ObjectChangeProvider))
        modelSnapshot.save()
        assert savedObjects.isEmpty()
    }

    @Test
    void "Test saving model with changed (dirty) object"(){
        def modelSnapshot = new GeneralUserModelSnapshotStub()
        def saved = false
        Date date = modelSnapshot.manageObject(new Date(), [doOnCommit:{saved = true}, doOnRollback:{}] as ObjectChangeProvider)
        date.setTime(0)
        modelSnapshot.save()
        assert saved
        assert date.getTime() == 0
    }

    @Test
    void "Test adding the same object multiple times to the model"(){
        def modelSnapshot = new GeneralUserModelSnapshotStub()
        def date = new Date()
        def dateReturned1 = modelSnapshot.manageObject(date, mock(ObjectChangeProvider))
        def dateReturned2 = modelSnapshot.manageObject(date, mock(ObjectChangeProvider))
        def dateReturned3 = modelSnapshot.manageObject(date, mock(ObjectChangeProvider))
        assert getSubjectOf(dateReturned1) == getSubjectOf(dateReturned2) &&
                getSubjectOf(dateReturned2) == getSubjectOf(dateReturned3)
        assert dateReturned1.equals(dateReturned2) && dateReturned2.equals(dateReturned3)
    }

    private Object getSubjectOf(proxy) {
        return proxy.@subject
    }

    @Test
    void "Test saving syncronized execution"(){
        def syncronizationBlockUsed = false
        def modelSnapshot = new GeneralUserModelSnapshotStub({syncronizationBlockUsed = true; it()})
        assert !syncronizationBlockUsed
        modelSnapshot.save()
        assert syncronizationBlockUsed
    }


    @Test
    void "Test notification of save for one listener"(){
        def modelSnapshot = new GeneralUserModelSnapshotStub()
        def notificationCalled = false
        def modelSnapshotListener = { notificationCalled = true } as UserSnapshotListener
        modelSnapshot.registerListener(modelSnapshotListener)
        modelSnapshot.save()
        assert notificationCalled
    }

    @Test
    void "Test notification of save for N listeners"(){
        def modelSnapshot = new GeneralUserModelSnapshotStub()
        def notification1Called = false
        def notification2Called = false
        def modelSnapshotListener1 = { notification1Called = true } as UserSnapshotListener
        def modelSnapshotListener2 = { notification2Called = true } as UserSnapshotListener
        modelSnapshot.registerListener(modelSnapshotListener1)
        modelSnapshot.registerListener(modelSnapshotListener2)
        modelSnapshot.save()
        assert notification1Called && notification2Called
    }

    @Test
    void "Test notification of save for N, but removing some"(){
        def modelSnapshot = new GeneralUserModelSnapshotStub()
        def notification1Called = false
        def notification2Called = false
        def modelSnapshotListener1 = { notification1Called = true } as UserSnapshotListener
        def modelSnapshotListener2 = { notification2Called = true } as UserSnapshotListener
        modelSnapshot.registerListener(modelSnapshotListener1)
        modelSnapshot.registerListener(modelSnapshotListener2)
        modelSnapshot.unregisterUnitOfWorkerListener(modelSnapshotListener2)
        modelSnapshot.save()
        assert notification1Called && !notification2Called
    }

    @Test
    void "Test unused notification"(){
        def modelSnapshot = new GeneralUserModelSnapshotStub()
        modelSnapshot.registerListener({ } as UserSnapshotListener)
        System.gc()
        assertWaitingSuccess({assert modelSnapshot.getlisteners().isEmpty()})
    }

    void addDateToModelAndChange(GeneralUserModelSnapshotStub modelSnapshot, objectToAdd, changeClosure) {
        def date = modelSnapshot.manageObject(objectToAdd, mock(ObjectChangeProvider))
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
            super({it()} as SynchronizationBlock)
        }

        GeneralUserModelSnapshotStub(SynchronizationBlock syncronizationBlock) {
            super(syncronizationBlock)
        }

        @Override
        void rollback() {}
    }
}
