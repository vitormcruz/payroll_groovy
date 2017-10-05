package com.vmc.concurrency

import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail

class TrackObjectUnitTest {

    @Test
    void "Test creation of a track object from one of it's constructors"(){
        def ex = shouldFail({new TrackObject(new Date(), {}, UUID.randomUUID())})
        assert ex.message == "I must be created using one of my factory methods. You should not use my constructor."
    }

}
