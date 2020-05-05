package com.vmc.payroll.domain.unionAssociation


import org.junit.jupiter.api.Test

import static groovy.test.GroovyAssert.shouldFail

class NoUnionAssociationUnitTest {

    @Test
    void "Get the unique reference to the NoUnionAssociation is ok"(){
        assert NoUnionAssociation.getInstance() != null
    }

    @Test
    void "Create a NoUnionAssociation fails"(){
        assert shouldFail(UnsupportedOperationException, {new NoUnionAssociation()}).message == "I am a singleton, please get my instance thought the getInstance method."
    }
}
