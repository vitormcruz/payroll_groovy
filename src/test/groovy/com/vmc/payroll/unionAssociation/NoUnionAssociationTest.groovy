package com.vmc.payroll.unionAssociation

import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail

class NoUnionAssociationTest {

    @Test
    void "Get the unique reference to the NoUnionAssociation is ok"(){
        assert NoUnionAssociation.getInstance() != null
    }

    @Test
    void "Create a NoUnionAssociation fails"(){
        def ex = shouldFail(UnsupportedOperationException, {new NoUnionAssociation()})
        assert ex.message == "I am a singleton, please get my instance thought the getInstance method."
    }
}
