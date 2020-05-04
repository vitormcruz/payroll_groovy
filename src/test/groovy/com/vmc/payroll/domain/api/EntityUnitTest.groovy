package com.vmc.payroll.domain.api

import nl.jqno.equalsverifier.EqualsVerifier
import org.apache.commons.lang3.builder.EqualsBuilder
import org.junit.Test

class EntityUnitTest {

    @Test
    def void equalsContract() {
        EqualsVerifier.forClass(FakeEqualsTestEntity)
                      .withRedefinedSubclass(FakeEqualsAddedStateTestEntity).withOnlyTheseFields("id")
                      .verify()
    }

    static class FakeEqualsTestEntity implements EntityCommonTrait{
        final String id = UUID.randomUUID()

        @Override
        def getEntityClass() {
            return FakeEqualsTestEntity
        }
    }

    static class FakeEqualsAddedStateTestEntity extends FakeEqualsTestEntity{
        final String addedState = UUID.randomUUID()

        boolean equals(Object that) {
            if(that == null) return false
            if(that.is(this)) return true
            if(!this.getClass().isInstance(that)) return false
            if(!that.canEqual(this)) return false
            return new EqualsBuilder().appendSuper(super.equals(that)).append(this.getAddedState(), that.getAddedState()).isEquals()
        }

        @Override
        def getEntityClass() {
            return FakeEqualsAddedStateTestEntity
        }
    }
}
