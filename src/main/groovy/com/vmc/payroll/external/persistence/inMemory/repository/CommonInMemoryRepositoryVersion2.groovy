package com.vmc.payroll.external.persistence.inMemory.repository


import com.vmc.payroll.domain.api.Entity
import com.vmc.payroll.domain.api.Repository

class CommonInMemoryRepositoryVersion2<E extends Entity> extends AbstractCollection<E> implements Repository<E> {

    @Delegate
    protected Collection<E> entities
    protected Map entitiesById

    CommonInMemoryRepositoryVersion2() {
        entitiesById = new HashMap()
        entities = entitiesById.values()
    }

    @Override
    E get(Object id) {
        return entitiesById.get(id)
    }

    boolean add(E entity) {
        entitiesById.put(entity.getId(), entity)
        return true
    }

    @Override
    void update(E employee) {
        entitiesById.remove(employee.getId())
        entitiesById.put(employee.getId(), employee)
    }

    boolean remove(entity) {
        entitiesById.remove(entity.getId())
    }

    void clear() {
        super.clear()
        entitiesById.clear()
    }
}
