package com.vmc.validationNotification.builder.imp

import com.vmc.validationNotification.testPreparation.ValidationNotificationTestSetup
import groovy.test.GroovyAssert
import org.junit.Test

import static com.vmc.validationNotification.ApplicationValidationNotifier.issueError

class DataSetBuilderUnitTest extends ValidationNotificationTestSetup{

    @Test
    void "The DataSetBuilder class parameter must be provided"(){
        def ex = GroovyAssert.shouldFail IllegalArgumentException, {getDataSetBuilder(null, {})}
        assert ex.message == "A class to build must be provided"
    }

    @Test
    void "The DataSetBuilder insertCommand parameter must be provided"(){
        def ex = GroovyAssert.shouldFail IllegalArgumentException, {getDataSetBuilder(BuildedClass, null)}
        assert ex.message == "An insertCommand closure must be provided"
    }

    @Test
    void "DataSetbuilder must be immutable"(){
        def dataSetBuilder = getDataSetBuilder(BuildedClass, {})
        assert dataSetBuilder.setName() != dataSetBuilder
    }

    @Test
    void "DataSetbuilder buildAndDoOnSuccess requesty must execute insertCommand"(){
        def inserted = false
        getDataSetBuilder(BuildedClass, {inserted = true}).buildAndDoOnSuccess({})
        assert inserted : "Builder method should have called insertCommand."
    }

    @Test
    void "DataSetbuilder buildAndDo requesty must execute insertCommand"(){
        def inserted = false
        getDataSetBuilder(BuildedClass, {inserted = true}).buildAndDo({}, {})
        assert inserted : "Builder method should have called insertCommand."
    }

    @Test
    void "DataSetbuilder buildAndDoOnFailure requesty must execute insertCommand"(){
        def inserted = false
        getDataSetBuilder(BuildedClass, {inserted = true}).buildAndDoOnFailure({})
        assert inserted : "Builder method should have called insertCommand."
    }

    @Test
    void "DataSetbuilder build requesty must execute insertCommand"(){
        def inserted = false
        getDataSetBuilder(BuildedClass, {inserted = true}).build()
        assert inserted : "Builder method should have called insertCommand."
    }

    @Test
    void "InsertCommand should be called upon success"(){
        def inserted = false
        getDataSetBuilder(BuildedClass, {inserted = true}).build()
        assert inserted : "InsertCommand was not called when the build process succeeded."
    }

    @Test
    void "InsertCommand should not be called upon failure"(){
        def inserted = false
        getDataSetBuilder(BuildedClass, {inserted = true}).setError("Error").build()
        assert !inserted : "InsertCommand was called when the build process failed."
    }



    def getDataSetBuilder(Class aClass, Closure insertCommand){
        return new DataSetBuilder(aClass, insertCommand)
    }

    static class BuildedClass {

        void setError(String error){
            issueError(this, [:], error)
        }

    }

}
