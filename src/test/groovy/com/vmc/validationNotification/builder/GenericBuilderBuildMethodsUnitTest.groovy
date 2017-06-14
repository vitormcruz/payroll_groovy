package com.vmc.validationNotification.builder

import com.vmc.validationNotification.builder.imp.GenericBuilder
import com.vmc.validationNotification.builder.imp.UsedForbiddenConstructor
import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail
import static org.junit.Assert.fail

class GenericBuilderBuildMethodsUnitTest extends ValidationNotificationTestSetup{

    @Test
    void "The GenericBuilder class parameter must be provided"(){
        def ex = shouldFail IllegalArgumentException, {getBuilderFor(null)}
        assert ex.message == "A class to build must be provided"
    }

    @Test
    void "Call onSuccessDoWithBuiltEntity with a success built entity "(){
        def buildersForTest = getSuccessBuilders()
        buildersForTest.each { builderForTest ->
            def builtEntity
            builderForTest.buildAndDoOnSuccess { builtEntity = it }
            assert builtEntity != null : "Upon success, entity should not be null"
            assert builtEntity.getAttribute() == "ok"
        }
    }

    @Test
    void "Call onSuccessDoWithBuiltEntity with a failed built entity "(){
        def buildersForTest = getFailBuilders()
        buildersForTest.each { builderForTest ->
            builderForTest.buildAndDoOnSuccess { fail("Issued success but the entity was created with an error.") }
            assert builderForTest.build() == null: "null should be returned upon build failure"
        }
    }

    @Test
    void "Call onSuccessDoWithBuiltEntity should return null with a failed built entity"(){
        def buildersForTest = getFailBuilders()
        buildersForTest.each { builderForTest ->
            def result = builderForTest.buildAndDoOnSuccess {}
            assert result == null: "Call onSuccessDoWithBuiltEntity should return null with a failed built entity"
        }
    }

    @Test
    void "Call onSuccessDoWithBuiltEntity should return the built entity when build was successful"(){
        def buildersForTest = getSuccessBuilders()
        buildersForTest.each { builderForTest ->
            def result = builderForTest.buildAndDoOnSuccess {}
            assert result != null: "Call onSuccessDoWithBuiltEntity should return the built entity when build was successful"
            assert result.getAttribute() == "ok"
        }
    }

    @Test
    void "Call buildAndDo with a success built entity"(){
        def buildersForTest = getSuccessBuilders()
        buildersForTest.each { builderForTest ->
            builderForTest.buildAndDo({ assert it.getAttribute() == "ok" },
                    { fail("Issued success but the entity was created with an error.") })
        }
    }

    @Test
    void "Call buildAndDoOnSuccess with a failed built entity "(){
        def buildersForTest = getFailBuilders()
        buildersForTest.each { builderForTest ->
            builderForTest.buildAndDoOnSuccess { fail("Issued success but the entity was created with an error.") }
            assert builderForTest.build() == null: "null should be returned upon build failure"
            def result = builderForTest.buildAndDo({
                fail("Issued success but the entity was created with an error.")
            }, {})
            assert result == null: "In case of error the builder should return null."
        }
    }

    @Test
    void "Call buildAndDo should return null with a failed built entity"(){
        def buildersForTest = getFailBuilders()
        buildersForTest.each { builderForTest ->
            def result = builderForTest.buildAndDo({}, {})
            assert result == null: "Call buildAndDo should return null with a failed built entity"
        }
    }

    @Test
    void "Call buildAndDo should return the built entity when build was successful"(){
        def buildersForTest = getSuccessBuilders()
        buildersForTest.each { builderForTest ->
            def result = builderForTest.buildAndDo({}, {})
            assert result != null: "Call buildAndDo should return the built entity when build was successful"
            assert result.getAttribute() == "ok"
        }
    }

    @Test
    void "Builder should not call forbidden constructors"(){
        def ex = shouldFail UsedForbiddenConstructor, {getBuilderFor(TestForbiddenConstructor).build()}
        assert ex.message == "The constructor found for TestForbiddenConstructor with [] arguments is of forbidden use."
        ex = shouldFail UsedForbiddenConstructor, {getBuilderFor(TestForbiddenConstructor).withString("test").build()}
        assert ex.message == "The constructor found for TestForbiddenConstructor with [String] arguments is of forbidden use."
    }

    @Test
    void "Builder should not call forbidden constructors, even with null arguments"(){
        def ex = shouldFail UsedForbiddenConstructor, {getBuilderFor(TestForbiddenConstructor).withString(null).build()}
        assert ex.message == "The constructor found for TestForbiddenConstructor with [NullObject] arguments is of forbidden use."
    }

    @Test
    void "Builder should work with all allowed constructors"(){
        [Integer, Byte, Long].forEach {
            def generatedEntity = getBuilderFor(TestForbiddenConstructor).with(it.valueOf("1")).build()
            assert generatedEntity != null : "Entity should be build for constructor with ${it.simpleName}, but wasn't"
        }
    }

    def getSuccessBuilders(){
        return [getBuilderFor(TestEntity).setAttribute("ok"),
                getBuilderFor(TestConstructorWithOneArgument).withAttribute("ok")]
    }

    def getFailBuilders(){
        return [getBuilderFor(TestEntity).setAttribute("fail"),
                getBuilderFor(TestConstructorWithOneArgument).withAttribute("fail")]
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

    static class TestConstructorWithOneArgument {
        String attribute

        TestConstructorWithOneArgument(String attribute) {
            if(attribute == "fail"){
                issueError("error")
            }else{
                this.attribute = attribute
            }
        }
    }

    static class TestForbiddenConstructor implements BuilderAwareness{
        private TestForbiddenConstructor(){invalidForBuilder()}
        private TestForbiddenConstructor(String a){invalidForBuilder()}
        protected TestForbiddenConstructor(Integer a){}

        TestForbiddenConstructor(Byte a){}

        TestForbiddenConstructor(Long a){}
    }
}
