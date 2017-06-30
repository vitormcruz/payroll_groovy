package com.vmc.validationNotification.builder

import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail

class FillingGenericBuilderUnitTest extends ValidationNotificationTestSetup{

    @Test
    void "Using one with to call constructor in the correct order"(){
        def entity = getBuilderFor(TestNPropertiesOneConstructor).withA("a").withB("b").with1(1).with1Long(1L).build()
        assert entity != null
        assert entity instanceof TestNPropertiesOneConstructor
    }

    @Test
    void "Using one with to call constructor in the wrong order"(){
        def error = shouldFail GroovyRuntimeException, {getBuilderFor(TestNPropertiesOneConstructor).withA("a").with1(1).with1Long(1L).withB("b").build()}
        assert error.message.contains("Could not find matching constructor")
    }

    @Test
    void "Using one with to call constructor using less arguments than the constructor has"(){
        def error = shouldFail GroovyRuntimeException, {getBuilderFor(TestNPropertiesOneConstructor).withA("a").withB("b").with1(1).build()}
        assert error.message.contains("Could not find matching constructor")
    }

    @Test
    void "Using one with to call constructor using more arguments than the constructor has"(){
        def error = shouldFail GroovyRuntimeException, {getBuilderFor(TestNPropertiesOneConstructor).withA("a").withB("b").with1(1).with1Long(1L).withC("c").build()}
        assert error.message.contains("Could not find matching constructor")
    }

    @Test
    void "N withs in the correct order for K arguments, where N <> K"(){
        def error = shouldFail GroovyRuntimeException, {getBuilderFor(TestNPropertiesOneConstructor).withA("a")
                                                                                                          .withRest("b", 1, 1L, "c")
                                                                                                          .build()}
        assert error.message.contains("Could not find matching constructor")
    }

    @Test
    void "N withs in the correct order for K arguments, where N == K"(){
        def object = getBuilderFor(TestNPropertiesOneConstructor).withA("a")
                                                                 .withB("b")
                                                                 .withC(1)
                                                                 .withD(1L)
                                                                 .build()
        assert object != null
        assert object instanceof TestNPropertiesOneConstructor
    }

    @Test
    void "N withs in the incorrect order for K arguments, where N == K"(){
        def error = shouldFail GroovyRuntimeException, {getBuilderFor(TestNPropertiesOneConstructor).with1(1)
                                                                                                          .withA("a")
                                                                                                          .withB("b")
                                                                                                          .withC(1L)
                                                                                                          .build()}
        assert error.message.contains("Could not find matching constructor")
    }

    @Test
    void "Repeat with giving wrong arguments"(){
        def error = shouldFail GroovyRuntimeException, {getBuilderFor(TestNPropertiesOneConstructor).withA("a")
                                                                                                          .with1(1)
                                                                                                          .with1Long(1L)
                                                                                                          .build()}
        assert error.message.contains("Could not find matching constructor")
    }

    @Test
    void "Repeat with giving right arguments"(){
        def entity = getBuilderFor(TestNPropertiesOneConstructor).withA("a").withB("b").withC(1).withD(1L).build()
        assert entity != null
        assert entity instanceof TestNPropertiesOneConstructor
    }

    @Test
    void "Builder used for constructor passing one null argument"(){
        def entity = getBuilderFor(TestConstructorWithNull).withName(null).build()
        assert entity != null : "When null is used, a constructor with the same number of arguments should be used"
    }

    @Test
    void "Builder used for constructor passing N null arguments"(){
        def entity = getBuilderFor(TestConstructorWithNull).withName(null)
                                                           .withAddress(null)
                                                           .build()
        assert entity != null : "When null is used, a constructor with the same number of arguments should be used"
    }

    @Test
    void "Mixing with and set"(){
        TestNPropertiesOneConstructor object = getBuilderFor(TestNPropertiesOneConstructor).withA("a")
                                                                                           .withB("b")
                                                                                           .withC(1)
                                                                                           .withD(1L)
                                                                                           .setA("Changed")
                                                                                           .build()

        assert object.getA() == "Changed"
    }

    @Test
    void "Repeat set"(){
        TestNPropertiesOneConstructor object = getBuilderFor(TestNPropertiesOneConstructor).withA("a")
                                                                                           .withB("b")
                                                                                           .withC(1)
                                                                                           .withD(1L)
                                                                                           .setA("Changed")
                                                                                           .setB("Changed")
                                                                                           .setB("Changed2")
                                                                                           .build()

        assert object.getA() == "Changed"
        assert object.getB() == "Changed2"
    }

    GenericBuilder getBuilderFor(Class clazz) {
        new GenericBuilder(clazz)
    }

    static class TestEntity{
        def attribute

        void setAttribute(attribute) {
            if(attribute == "fail"){
                issueError("error")
            }else{
                this.attribute = attribute
            }
        }
    }

    static class TestConstructorWithNull {
        TestConstructorWithNull(String a){}
        TestConstructorWithNull(String a, String b){}
    }

    static class TestNPropertiesOneConstructor {
        String a
        String b
        int c
        long d

        TestNPropertiesOneConstructor(String a, String b, Integer c, Long d){
            this.d = d
            this.c = c
            this.b = b
            this.a = a
        }
    }
}
