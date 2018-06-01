package com.vmc.feature

import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail

class ObjectMementoFeatureTest {

    @Test
    void "Take a snapshot"(){
        SomeObject someObject = new SomeObject()
        SomeObject someObjectMemento = someObject.getMemento()
        assert !someObject.is(someObjectMemento)
        assert someObject.attributeA.is(someObjectMemento.attributeA)
        assert !someObject.attributeB.is(someObjectMemento.attributeB)
        assert someObject.attributeB == someObjectMemento.attributeB
        assert someObject.attributeC.is(someObjectMemento.attributeC)
        assert someObject.getCurrentObjectStateId() == someObjectMemento.getCurrentObjectStateId()
    }

    @Test
    void "Take a snapshot from a class should fail"(){
        def ex = shouldFail(UnsupportedOperationException, {SomeObject.getMemento()})
        assert ex.message == "Cannot make a memento of a class"
    }

    @Test
    void "Rollback a non memento object object"(){
        def object = new SomeObject()
        object.attributeA = "change"
        object.attributeB = [3, 2, 1]
        object.attributeA = 11
        assert object.getCurrentObjectStateId() == object.rollback().getCurrentObjectStateId()
    }

    @Test
    void "Rollback a memento object"(){
        def object = new SomeObject().getMemento()
        object.attributeA = "change"
        object.attributeB = [3, 2, 1]
        object.attributeC = 11
        object.rollback()
        assert object.attributeA == "test"
        assert object.attributeB == [1, 2, 3]
        assert object.attributeC == 10
    }

    @Test
    void "Test isDirty in a non memento object"(){
        assert new SomeObject().isDirty()
        assert new SomeObject().with {
            attributeA = "another"
            return it
        }.isDirty()
    }

    @Test
    void "Test isDirty in a memento object"(){
        assert !new SomeObject().getMemento().isDirty()
        assert new SomeObject().getMemento().with {
            attributeA = "another"
            return it
        }.isDirty()

        assert !new SomeObject().getMemento().with {
            attributeA = "another"
            return it
        }.rollback().isDirty()
    }

    static class SomeObject {
        def attributeA = "test"
        def attributeB = [1, 2, 3]
        def attributeC = 10
    }

}
