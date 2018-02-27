package com.vmc.concurrency

import com.vmc.concurrency.api.UserModelSnapshot
import com.vmc.concurrency.api.UserSnapshotListener
import com.vmc.payroll.domain.api.Entity
import com.vmc.payroll.domain.api.Repository
import org.apache.commons.collections4.IteratorUtils
import org.apache.commons.collections4.iterators.PeekingIterator

class UserSnapshotAwareRepository<E extends Entity> implements Repository<E>, UserSnapshotListener{

    @Delegate
    private Repository<E> repository
    private Set<E> snapshotAddedObjects = []
    private Set<E> snapshotRemovedObjects = []

    UserSnapshotAwareRepository(Repository<E> repository) {
        this.repository = repository
        UserModelSnapshot.instance.registerUnitOfWorkerListener(this)
    }

    @Override
    void update(E employee) {
        //Shouldn't be called.
    }

    @Override
    Iterator<E> iterator() {
        return new IteratorBoladao(IteratorUtils.chainedIterator(snapshotAddedObjects.iterator(), repository.iterator()), snapshotRemovedObjects)
    }

    @Override
    boolean add(E e) {
        return snapshotAddedObjects.add(e)
    }

    @Override
    boolean remove(Object o) {
        return false
    }

    @Override
    void clear() {

    }

    Collection<E> savedObjects() {
        return repository
    }

    @Override
    void saveCalled(UserModelSnapshot unitOfWork) {
        repository.addAll(snapshotAddedObjects)
        snapshotAddedObjects.clear()
//        repository.removeAll(snapshotAddedObjects)
    }

    static class IteratorBoladao<E> implements Iterator<E>{

        @Delegate
        private PeekingIterator<E> iterator
        private Collection removedObjects

        IteratorBoladao(Iterator<E> iterator, Collection removedObjects) {
            this.removedObjects = removedObjects
            this.iterator = IteratorUtils.peekingIterator(iterator)
        }

        @Override
        boolean hasNext(){
            while(iterator.hasNext() && removedObjects.contains(iterator.peek())){
                iterator.next()
            }

            return iterator.hasNext()
        }

        @Override
        E next(){
            while(iterator.hasNext() && removedObjects.contains(iterator.peek())){
                iterator.next()
            }

            return iterator.next()
        }
    }
}
