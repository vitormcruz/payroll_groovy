package sandbox.magritte.testGenerator

import org.junit.Ignore
import org.junit.Test
import sandbox.magritte.description.Description
import sandbox.magritte.methodGenerator.imp.SimpleGeneratedMethod
import sandbox.magritte.testGenerator.util.DescriptionForTest
import sandbox.magritte.testGenerator.util.TestGeneratorForTestDescriptionPartial

import static groovy.test.GroovyAssert.shouldFail
/**
 * Tests the description of test suits.
 */
class TestGeneratorForTestDescriptionTest {

    @Test
    def void "Create a test description for null class"(){
        def ex = shouldFail(IllegalArgumentException,
                            { new TestGeneratorForTestDescriptionPartial().descriptionsFor(null, [] as Description[]) })
        assert ex.message == "Cannot create test scenarios for a TestDescription that do not specify a target class"
    }

    @Test
    def void "Create a test description without specify any definition for the class under test"(){
        def testDescription = new TestGeneratorForTestDescriptionPartial().descriptionsFor(ClassUnderTest, [] as Description[])
        assert testDescription.getGeneratedMethods().isEmpty() : "No definition test suit description should not generate any test methods."
    }

    @Test
    @Ignore
    def void "Create a test description without specifying a validation clojure"(){}

    @Test
    @Ignore
    def void "Create a test description without specifying an object creation clojure"(){}

    @Test
    @Ignore
    def void "Create a test description specifying an object creation clojure"(){}

    @Test
    def void "Create a test description specifying one definition for the class under test"(){
        def testDescription = new TestGeneratorForTestDescriptionPartial().descriptionsFor(ClassUnderTest, new DescriptionForTest())
        assert DescriptionForTest.testScenarios as Set == testDescription.getGeneratedMethods() as Set :
                "A test suit with a class definition should generate test methods based on the description used."
    }

    @Test
    def void "Create a test description specifying N definitions for the class under test"(){
        def description = new TestGeneratorForTestDescriptionPartial().descriptionsFor(ClassUnderTest,
                                                                                new DescriptionForTest(),
                                                                                new DescriptionForTest2())
        def testScenariosEsperados = []
        testScenariosEsperados.addAll(DescriptionForTest2.testScenarios)
        testScenariosEsperados.addAll(DescriptionForTest.testScenarios)

        def testCases = description.getGeneratedMethods()
        assert testScenariosEsperados as Set == testCases as Set : "A test suit with a class definition should generate test methods based on the descriptions used."
    }

    public static class ClassUnderTest{
    }

    public static class DescriptionForTest2 implements Description{
        static def testScenarios = [new SimpleGeneratedMethod()]

        public Collection<SimpleGeneratedMethod> asTestScenariosFor(descriptedClass){
            testScenarios
        }

    }
}
