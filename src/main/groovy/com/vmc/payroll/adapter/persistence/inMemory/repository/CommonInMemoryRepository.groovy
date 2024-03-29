package com.vmc.payroll.adapter.persistence.inMemory.repository


import com.vmc.payroll.domain.api.Entity
import com.vmc.payroll.domain.api.Repository

class CommonInMemoryRepository<E extends Entity> extends AbstractCollection<E> implements Repository<E> {

    protected Map<Object, E> entitiesById

    CommonInMemoryRepository() {
        entitiesById = new HashMap()
    }

    @Override
    E get(id) {
        return entitiesById.get(id)
    }

    boolean add(E entity) {
        entitiesById.put(entity.getId(), entity)
        return true
    }

    boolean remove(entity) {
        entitiesById.remove(entity.getId())
    }

    void clear() {
        super.clear()
        entitiesById.clear()
    }

    @Override
    int size() {
        return entitiesById.values().size()
    }

    @Override
    Iterator<E> iterator() {
        return entitiesById.values().iterator()
    }
}
