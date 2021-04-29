package com.vmc.userModel

import com.vmc.objectMemento.ObjectChangeProvider
import org.junit.jupiter.api.Test

/** Tests more specific to the GeneralUserModel implementation **/
class GeneralUserModelTest {

    private modelSnapshot

    @Test
    void "Test removing objects while saving all of them - ConcurrentModificationException cannot happen"(){
        modelSnapshot = new GeneralUserModel_That_Forces_Save_and_Removal_Concurrently()
        5.times { modelSnapshot.manageObject(new Object(), [] as ObjectChangeProvider) }
        modelSnapshot.save()
        assert modelSnapshot.getManagedObjects().isEmpty()
    }

    @Test
    void "Test removing objects while rollingback all of them - ConcurrentModificationException cannot happen"(){
        modelSnapshot = new GeneralUserModel_That_Forces_Save_and_Removal_Concurrently()
        5.times { modelSnapshot.manageObject(new Object(), [] as ObjectChangeProvider) }
        modelSnapshot.rollback()
        assert modelSnapshot.getManagedObjects().isEmpty()
    }


}
