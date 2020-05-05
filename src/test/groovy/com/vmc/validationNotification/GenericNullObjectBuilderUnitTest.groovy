package com.vmc.validationNotification

import com.google.common.collect.HashMultimap
import com.google.common.collect.Maps
import com.google.common.collect.SetMultimap
import org.junit.jupiter.api.Test

import static GenericNullObjectBuilder.newNullObjectOf
import static groovy.test.GroovyAssert.shouldFail
import static org.junit.jupiter.api.Assertions.fail

class GenericNullObjectBuilderUnitTest {

    private errorsExpected = new HashMultimap<Map.Entry<String, Object>, String>().with {
        put(Maps.immutableEntry("context1", "value1"), "error1")
        put(Maps.immutableEntry("context2", "value2"), "error2")
        put(Maps.immutableEntry("context3", "value3"), "error3")
        it
    }

    @Test
    void "Test intantiating a new NullObject"(){
        def nullObject = newNullObjectOf(SuperClassNullObjectFake, errorsExpected)
        assert nullObject.getClass().simpleName == "GenericNullObjectBuilderUnitTest\$SuperClassNullObjectFake_NullObject"
    }

    @Test
    void "Test result answer to onBuild familly messages"(){
        def nullObject = newNullObjectOf(SuperClassNullObjectFake, errorsExpected)
        nullObject.onBuildSuccess {fail("GenericNullObjectBuilder result cannot be successful")}
        SetMultimap errosObtained
        nullObject.onBuildFailure {errosObtained = it}
        assert errorsExpected.asMap() == errosObtained.asMap()
    }
    
    @Test
    void "Test errors from different instances"(){
        def errorsExpected2 = new HashMultimap(errorsExpected).with {
            put(Maps.immutableEntry("context4", "value4"), "error4")
            it
        }
        def nullObjectErrorSet1 = newNullObjectOf(SuperClassNullObjectFake, errorsExpected)
        def nullObjectErrorSet2 = newNullObjectOf(SuperClassNullObjectFake, errorsExpected2)
        nullObjectErrorSet1.onBuildSuccess {fail("GenericNullObjectBuilder result cannot be successful")}
        nullObjectErrorSet2.onBuildSuccess {fail("GenericNullObjectBuilder result cannot be successful")}
        SetMultimap errosObtained1
        nullObjectErrorSet1.onBuildFailure {errosObtained1 = it}
        SetMultimap errosObtained2
        nullObjectErrorSet2.onBuildFailure {errosObtained2 = it}
        assert errorsExpected.asMap() == errosObtained1.asMap()
        assert errorsExpected2.asMap() == errosObtained2.asMap()
    }

    @Test
    void "Test result answer to normal messages"(){
        SuperClassNullObjectFake nullObject = newNullObjectOf(SuperClassNullObjectFake, errorsExpected)
        assert shouldFail {nullObject.method1()}.message == "I am a NullObject and I cannot respond to this message."
        assert shouldFail {nullObject.method2()}.message == "I am a NullObject and I cannot respond to this message."
    }

    @Test
    void "Test result answer to core methods that cannot fail"(){
        SuperClassNullObjectFake nullObject = newNullObjectOf(SuperClassNullObjectFake, errorsExpected)
        assert nullObject.toString().startsWith("com.vmc.validationNotification.GenericNullObjectBuilderUnitTest\$SuperClassNullObjectFake_NullObject@")
    }

    static class SuperSuperClassNullObjectFake {
        def method1(){}
    }

    static class SuperClassNullObjectFake extends SuperSuperClassNullObjectFake{
        def method2() {}
    }

}
