package com.vmc.userModel

import com.vmc.objectMemento.ObjectChangeProvider
import com.vmc.userModel.api.UserModelListener
import org.junit.jupiter.api.Test

//TODO adjust all naming to UserModel, i think it is better for it to have a similar behavior of a memento.
class UserModelUnitTest {

    public static final int TIMEOUT = 3000

    @Test
    void "Test obtaining referenced object from user model"(){
        def modelSnapshot = new GeneralUserModel()
        def date = modelSnapshot.manageObject(new Date(), [] as ObjectChangeProvider)
        System.gc()
        assertWaitingSuccess({modelSnapshot.getManagedObjects().contains(date)}, 5000)
    }

    @Test
    void "Test obtaining unreferenced and unchanged object from user model"(){
        def modelSnapshot = new GeneralUserModel()
        modelSnapshot.manageObject(new Date(), [] as ObjectChangeProvider)
        System.gc()
        assertWaitingSuccess({assert modelSnapshot.getManagedObjects().isEmpty()}, 5000)
    }

    @Test
    void "Test obtaining unreferenced but changed (dirty) object from user model"(){
        def modelSnapshot = new GeneralUserModel()
        addDateToModelAndChange(modelSnapshot, new Date(), {it.setTime(0)})
        System.gc()
        assertWaitingSuccess({
            assert !modelSnapshot.getManagedObjects().isEmpty() &&
                    modelSnapshot.getManagedObjects().first().getObject().getTime() == 0 :
                    "Dirty (changed) objects should be retained in the model"
        }, 5000)
    }

    @Test
    void "Test saving model with unchanged object"(){
        def modelSnapshot = new GeneralUserModel()
        def savedObjects = new HashSet()
        def date = modelSnapshot.manageObject(new Date(), [] as ObjectChangeProvider)
        modelSnapshot.save()
        assert savedObjects.isEmpty()
    }

    @Test
    void "Test saving model with changed (dirty) object"(){
        def modelSnapshot = new GeneralUserModel()
        def saved = false
        Date date = modelSnapshot.manageObject(new Date(), [doOnCommit:{saved = true}, doOnRollback:{}] as ObjectChangeProvider)
        date.setTime(0)
        modelSnapshot.save()
        assert saved
        assert date.getTime() == 0
    }

    @Test
    void "Test adding the same object multiple times to the model"(){
        def modelSnapshot = new GeneralUserModel()
        def date = new Date()
        def dateReturned1 = modelSnapshot.manageObject(date, [] as ObjectChangeProvider)
        def dateReturned2 = modelSnapshot.manageObject(date, [] as ObjectChangeProvider)
        def dateReturned3 = modelSnapshot.manageObject(date, [] as ObjectChangeProvider)
        assert getSubjectOf(dateReturned1) == getSubjectOf(dateReturned2) &&
                getSubjectOf(dateReturned2) == getSubjectOf(dateReturned3)
        assert dateReturned1.equals(dateReturned2) && dateReturned2.equals(dateReturned3)
    }

    private Object getSubjectOf(proxy) {
        return proxy.@subject
    }

    @Test
    void "Test notification of save for one listener"(){
        def modelSnapshot = new GeneralUserModel()
        def notificationCalled = false
        def modelSnapshotListener = { notificationCalled = true } as UserModelListener
        modelSnapshot.registerListener(modelSnapshotListener)
        modelSnapshot.save()
        assert notificationCalled
    }

    @Test
    void "Test notification of save for N listeners"(){
        def modelSnapshot = new GeneralUserModel()
        def notification1Called = false
        def notification2Called = false
        def modelSnapshotListener1 = { notification1Called = true } as UserModelListener
        def modelSnapshotListener2 = { notification2Called = true } as UserModelListener
        modelSnapshot.registerListener(modelSnapshotListener1)
        modelSnapshot.registerListener(modelSnapshotListener2)
        modelSnapshot.save()
        assert notification1Called && notification2Called
    }

    @Test
    void "Test notification of save for N, but removing some"(){
        def modelSnapshot = new GeneralUserModel()
        def notification1Called = false
        def notification2Called = false
        def modelSnapshotListener1 = { notification1Called = true } as UserModelListener
        def modelSnapshotListener2 = { notification2Called = true } as UserModelListener
        modelSnapshot.registerListener(modelSnapshotListener1)
        modelSnapshot.registerListener(modelSnapshotListener2)
        modelSnapshot.unregisterListener(modelSnapshotListener2)
        modelSnapshot.save()
        assert notification1Called && !notification2Called
    }

    @Test
    void "Test unused notification"(){
        def modelSnapshot = new GeneralUserModel()
        modelSnapshot.registerListener({ } as UserModelListener)
        System.gc()
        assertWaitingSuccess({assert modelSnapshot.getListeners().isEmpty()}, 5000)
    }

    void addDateToModelAndChange(GeneralUserModel modelSnapshot, objectToAdd, changeClosure) {
        def date = modelSnapshot.manageObject(objectToAdd, [] as ObjectChangeProvider)
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
