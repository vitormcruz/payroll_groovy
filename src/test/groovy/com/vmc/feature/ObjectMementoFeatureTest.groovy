package com.vmc.feature

import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail

class ObjectMementoFeatureTest {

    @Test
    void "Take a snapshot from a class should fail"(){
        def ex = shouldFail(UnsupportedOperationException, {SomeObject.takeSnapshot()})
        assert ex.message == "Cannot make a memento of a class"
    }

    @Test
    void "Test has snapshot history - takeSnapshot never called"(){
        SomeObject someObject = new SomeObject()
        assert !someObject.hasSnapshotHistory()
    }

    @Test
    void "Test has snapshot history - takeSnapshot called once"(){
        SomeObject someObject = new SomeObject()
        someObject.takeSnapshot()
        assert someObject.hasSnapshotHistory()
    }

    @Test
    void "Test has snapshot history - takeSnapshot called N times"(){
        SomeObject someObject = new SomeObject()
        someObject.takeSnapshot()
        someObject.takeSnapshot()
        someObject.takeSnapshot()
        assert someObject.hasSnapshotHistory()
    }

    @Test
    void "Test has snapshot history - takeSnapshot and then rollback"(){
        SomeObject someObject = new SomeObject()
        someObject.takeSnapshot()
        someObject.rollback()
        assert !someObject.hasSnapshotHistory()
    }

    @Test
    void "Test isDirty - takeSnapshot neverCalled"(){
        SomeObject someObject = new SomeObject()
        assert !someObject.isDirty()
    }

    @Test
    void "Test isDirty - takeSnapshot called, but no change made"(){
        SomeObject someObject = new SomeObject()
        someObject.takeSnapshot()
        assert !someObject.isDirty()
    }

    @Test
    void "Test isDirty - takeSnapshot called, changing one field"(){
        SomeObject someObject = new SomeObject()
        someObject.takeSnapshot()
        someObject.attributeA = "changed"
        assert someObject.isDirty()
    }

    @Test
    void "Test isDirty - takeSnapshot called, changing all fields"(){
        SomeObject someObject = new SomeObject()
        someObject.takeSnapshot()
        someObject.attributeA = "changed"
        someObject.attributeB = [3, 2, 1]
        someObject.attributeC = 11
        assert someObject.isDirty()
    }

    @Test
    void "Test isDirty - takeSnapshot called, changing one field but then take another snapshot"(){
        SomeObject someObject = new SomeObject()
        someObject.takeSnapshot()
        someObject.attributeA = "changed"
        someObject.takeSnapshot()
        assert !someObject.isDirty()
    }

    @Test
    void "Test isDirty - takeSnapshot called, changing one field, take another snapshot and rollback from this one"(){
        SomeObject someObject = new SomeObject()
        someObject.takeSnapshot()
        someObject.attributeA = "changed"
        someObject.takeSnapshot()
        someObject.rollback()
        assert someObject.isDirty()
    }

    @Test
    void "Test rollback - takeSnapshot never called"(){
        SomeObject someObject = new SomeObject()
        someObject.rollback()
        assert someObject.attributeA == "test"
        assert someObject.attributeB == [1, 2, 3]
        assert someObject.attributeC == 10
    }

    @Test
    void "Test rollback - takeSnapshot called and object changed"(){
        SomeObject someObject = new SomeObject()
        someObject.takeSnapshot()
        someObject.attributeA = "changed"
        someObject.attributeB = [3, 2, 1]
        someObject.attributeC = 11
        someObject.rollback()

        assert someObject.attributeA == "test"
        assert someObject.attributeB == [1, 2, 3]
        assert someObject.attributeC == 10
    }

    @Test
    void "Test rollback - takeSnapshot called, object changed and snapshot taken again"(){
        SomeObject someObject = new SomeObject()
        someObject.takeSnapshot()
        someObject.attributeA = "changed"
        someObject.attributeB = [3, 2, 1]
        someObject.attributeC = 11
        someObject.takeSnapshot()
        someObject.rollback()

        assert someObject.attributeA == "changed"
        assert someObject.attributeB == [3, 2, 1]
        assert someObject.attributeC == 11
    }

    @Test
    void "Test rollback twice- takeSnapshot called, object changed and snapshot taken again"(){
        SomeObject someObject = new SomeObject()
        someObject.takeSnapshot()
        someObject.attributeA = "changed"
        someObject.attributeB = [3, 2, 1]
        someObject.attributeC = 11
        someObject.takeSnapshot()
        someObject.rollback()
        someObject.rollback()

        assert someObject.attributeA == "test"
        assert someObject.attributeB == [1, 2, 3]
        assert someObject.attributeC == 10
    }

    //TODO add scenarios with commit action

    static class SomeObject {
        def attributeA = "test"
        def attributeB = [1, 2, 3]
        def attributeC = 10
    }

}
