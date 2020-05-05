package com.vmc.objectMother.imp

import com.vmc.objectMother.ObjectMother
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.jupiter.api.Test

import static groovy.test.GroovyAssert.shouldFail

class ObjectMotherUnitTest extends ValidationNotificationTestSetup{

    @Test
    void "Class parameter must be provided"(){
        assert shouldFail(IllegalArgumentException, {getObjectMother(null, {})}).message == "A class to build must be provided"
    }

    @Test
    void "Test scripts receives an instance of the class beeing built"(){
        def result = []
        getObjectMother(BuildedClass, {result.add(it instanceof BuildedClass)})
                                         .addBirthScript({result.add(it instanceof BuildedClass)})
                                         .addBirthScript({result.add(it instanceof BuildedClass)})
                                         .addBirthScript({result.add(it instanceof BuildedClass)})
                                         .createNewBorn()

        assert result == [true, true, true, true]
    }

    @Test
    void "Test createNewBornWithScript receives an instance of the class beeing built"(){
        def result = false
        getObjectMother(BuildedClass, {}).createNewBornWithScript({result = it instanceof BuildedClass})

        assert result
    }


    @Test
    void "Test createNewBornWithScript"(){
        def stringObtained = ""
        getObjectMother(BuildedClass, {stringObtained += "and the post build script"}).addBirthScript({stringObtained += "a" })
                                         .addBirthScript({stringObtained += "b" })
                                         .addBirthScript({stringObtained += "c" })
                                         .createNewBornWithScript({stringObtained = "ignored all birth scripts, executed this one "})

        assert stringObtained == "ignored all birth scripts, executed this one and the post build script" : "createNewBornWithScript method should have have ignored all previous birth " +
                                                                                                      "scripts configured except the post build."
    }


    @Test
    void "Create new born must execute the list of birth scripts in order"(){
        def stringObtained = ""
        getObjectMother(BuildedClass, {stringObtained += "d"}).addBirthScript({stringObtained += "a" })
                                                              .addBirthScript({stringObtained += "b" })
                                                              .addBirthScript({stringObtained += "c" })
                                                              .createNewBorn()
        assert stringObtained == "abcd" : "Builder method should have called birth scripts in order"
    }

    @Test
    void "Create new born must execute postscript on success"(){
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

    def getObjectMother(Class aClass, Closure postBirthScript){
        return new ObjectMother(aClass).configurePostBirthScript(postBirthScript)
    }

    static class BuildedClass {

        void setError(String error){
            issueError(error)
        }

    }

}
