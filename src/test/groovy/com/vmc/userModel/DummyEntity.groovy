package com.vmc.userModel

import com.vmc.payroll.domain.api.EntityCommonTrait


class DummyEntity implements EntityCommonTrait{

    private String id
    String field1

    DummyEntity(){

    }

    DummyEntity(String id) {
        this.id = id
    }

    DummyEntity(String id, String field1) {
        this.id = id
        this.field1 = field1
    }

    @Override
    def getId() {
        return id
    }
}
