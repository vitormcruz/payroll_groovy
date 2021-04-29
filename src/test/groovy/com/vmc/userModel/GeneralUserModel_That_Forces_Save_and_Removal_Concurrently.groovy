package com.vmc.userModel

import java.time.Instant

class GeneralUserModel_That_Forces_Save_and_Removal_Concurrently extends GeneralUserModel {
    def removeCalled = false
    def delayedRemoveOperation = []
    private Instant removeStartTime
    private Instant removeEndTime
    private Instant actionStartTime
    private Instant actionEndTime

    @Override
    void save() {
        doActionWhileRemovingManagedObjects({super.save()})
    }

    @Override
    void rollback() {
        doActionWhileRemovingManagedObjects({super.rollback()})
    }

    private void doActionWhileRemovingManagedObjects(action) {
        waitGCPromptsRemovalObjectManagement()
        Thread.start {
            removeStartTime = Instant.now()
            delayedRemoveOperation.each { it() }
            removeEndTime = Instant.now()
        }

        actionStartTime = Instant.now()
        action()
        actionEndTime = Instant.now()

        if (!likelyCreatedRaceCondition()) throw new RuntimeException(
                /This test likely did not reproduce the circumstance where an object is removed from management
                while all managed objects are being saved, which can result in a ConcurrentModificationException.
                You may try to add more managed objects to increase the likelihood of this particular race condition 
                to happen.
                
                This test is a best attempt to verify that a ConcurrentModificationException is happening. To do so,
                it must:
                
                    1- Fail to a thrown ConcurrentModificationException;
                    2- Create a race condition where it is very likely to happens both the removal of managed objects,
                procedure that is prompted concurrently by the GC, and the saving of all managed objects (save method) 
                at the same time.
               
               JUnit fails at 1 by default, and likelyCreatedRaceCondition verify if the needed condition for a 
               ConcurrentModificationException to be thrown was met/)
    }

    private void waitGCPromptsRemovalObjectManagement() {
        System.gc()
        while (!removeCalled) {
            sleep(100)
        }
    }

    @Override
    void removeUnusedObjectIfNotDirty(ManagedObject managedObject){
        removeCalled = true
        delayedRemoveOperation.add({super.removeUnusedObjectIfNotDirty(managedObject)})
    }

    Boolean likelyCreatedRaceCondition(){
        return removeStartTime.isBefore(actionEndTime) && removeEndTime.isAfter(actionStartTime) ||
               actionStartTime.isBefore(removeEndTime) && actionEndTime.isAfter(removeStartTime)
    }
}
