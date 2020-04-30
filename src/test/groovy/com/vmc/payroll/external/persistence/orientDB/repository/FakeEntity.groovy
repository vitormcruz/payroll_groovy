package com.vmc.payroll.external.persistence.orientDB.repository

import com.vmc.payroll.domain.api.EntityCommonTrait


class FakeEntity implements EntityCommonTrait{

    String id

    FakeEntity() {
    }

    FakeEntity(String id) {
        this.id = id
    }

    @Override
    def getEntityClass() {
        return FakeEntity
    }
}
