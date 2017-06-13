package com.vmc.payroll.external.persistence.inMemory.repository

import com.vmc.payroll.api.Entity
import com.vmc.payroll.api.Repository

class CommonInMemoryRepository<E extends Entity> implements Repository<E> {

    @Delegate
    private Collection<E> entities
    private Map entitiesById
    private pending = []

    CommonInMemoryRepository() {
        this.entitiesById = new HashMap()
        this.entities = entitiesById.values()
    }

    void executeAllPending(){
        pending.each {it()}
        pending.clear()
    }

    @Override
    E get(Object id) {
        return entitiesById.get(id)
    }

    @Override
    boolean add(E entity) {
        pending.add({
            entitiesById.put(entity.getId(), entity)
        })
        return false
    }

    @Override
    boolean addAll(Collection<? extends E> c) {
        return false
    }

    @Override
    void update(E employee) {
        pending.add({
            entitiesById.remove(employee.getId())
            entitiesById.put(employee.getId(), employee)
        })
    }

    @Override
    boolean remove(entity) {
        pending.add({
            entitiesById.remove(entity.getId())
        })
    }

    @Override
    boolean removeAll(Collection<?> c) {
        return false
    }

    @Override
    boolean retainAll(Collection<?> c) {
        return false
    }

    @Override
    void clear() {
        pending.add({
            entitiesById.clear()
        })
    }
}
