package com.vmc.validationNotification.objectCreation.imp

import com.vmc.validationNotification.objectCreation.ObjectMother
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail

class ObjectMotherUnitTest extends ValidationNotificationTestSetup{

    @Test
    void "Class parameter must be provided"(){
        assert shouldFail(IllegalArgumentException, {getObjectMother(null, {})}).message == "A class to build must be provided"
    }

    @Test
    void "Create new born requesty must execute postscript on success"(){
        def inserted = false
        getObjectMother(BuildedClass, {inserted = true}).createNewBorn()
        assert inserted : "Builder method should have called postBirthScript."
    }

    @Test
    void "InsertCommand should not be called upon failure"(){
        def inserted = false
        ObjectMother<BuildedClass> objectMother = getObjectMother(BuildedClass, { inserted = true }).addBirthScript {setError("Error")}
        objectMother.createNewBorn()
        assert !inserted : "Post script command was called when the build process failed."
    }

    def getObjectMother(Class aClass, Closure insertCommand){
        return new ObjectMother(aClass).configurePostBirthScript(insertCommand)
    }

    static class BuildedClass {

        void setError(String error){
            issueError(error)
        }

    }

}
