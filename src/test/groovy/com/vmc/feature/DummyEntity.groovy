package com.vmc.feature

import com.vmc.payroll.domain.api.EntityCommonTrait


class DummyEntity implements EntityCommonTrait{

    private String id
    String field1

    DummyEntity(String id) {
        this.id = id
    }

    @Override
    def getId() {
        return id
    }
}
