package com.vmc.concurrency.defautImpl

import org.junit.Test

class ObjectSnapshotExtensionsUnitTest {

    @Test
    def void "Take a snapshot"(){
        def object = new TestSnapshotClass()
        def objectSnapshot = object.takeSnapshot()
        assert objectSnapshot.state1.is(object.state1)
        assert !objectSnapshot.state2.is(object.state2)
        assert objectSnapshot.state2 == object.state2
    }

    @Test
    def void "Rollback the actual object"(){
        def object = new TestSnapshotClass()
        def rolledBackObject = object.rollbackSnapshot()
        assert rolledBackObject.is(object)
    }

    @Test
    def void "Rollback a snapshot object"(){
        def object = new TestSnapshotClass()
        def rolledBackObject = object.takeSnapshot().rollbackSnapshot()
        assert rolledBackObject.is(object)
    }

    //Test weak hash map

    static class TestSnapshotClass{
        String state1 = 1
        Collection state2 = [1,2,3]
    }
}
