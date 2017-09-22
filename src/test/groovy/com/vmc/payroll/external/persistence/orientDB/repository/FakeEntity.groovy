package com.vmc.payroll.external.persistence.orientDB.repository

import com.vmc.payroll.domain.api.Entity


class FakeEntity implements Entity{

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
