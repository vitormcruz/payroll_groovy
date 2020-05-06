package com.vmc.userModel


import com.vmc.objectMemento.ObjectChangeProvider
import com.vmc.payroll.domain.api.Entity
import com.vmc.payroll.domain.api.Repository
import com.vmc.userModel.api.UserModel
import com.vmc.userModel.api.UserModelListener
import org.apache.commons.collections4.IteratorUtils
import org.apache.commons.collections4.iterators.PeekingIterator

class UserModelAwareRepository<E extends Entity> extends AbstractCollection implements Repository<E>, UserModelListener{

    private Repository<E> repository
    private UserModel userModel
    private ObjectChangeProvider objectChangeProvider

    private Set<E> snapshotAddedObjects = []
    private Set<E> snapshotRemovedObjects = []

    UserModelAwareRepository(Repository<E> repository, UserModel userModel, ObjectChangeProvider objectChangeProvider) {
        this.repository = repository
        this.userModel = userModel
        this.userModel.registerListener(this)
        this.objectChangeProvider = objectChangeProvider;
    }

    @Override
    Iterator<E> iterator() {
        return new UserSnapshotAwareIterator(IteratorUtils.chainedIterator(snapshotAddedObjects.iterator(), repository.iterator()), snapshotRemovedObjects)
    }

    @Override
    boolean add(E e) {
        return snapshotAddedObjects.add(e)
    }

    @Override
    E get(id){
        def object = repository.get(id)
        return object == null ? null :  userModel.manageObject(object, objectChangeProvider)
    }

    @Override
    boolean remove(Object o) {
        if(repository.contains(o)){
            snapshotRemovedObjects.add(o)
            return true
        }
        return false
    }

    @Override
    int size(){
        return repository.size() + snapshotAddedObjects.size() - snapshotRemovedObjects.size()
    }

    Collection<E> savedObjects() {
        return repository
    }

    @Override
    void saveCalled(UserModel unitOfWork) {
        repository.addAll(snapshotAddedObjects)
        snapshotAddedObjects.clear()
        repository.removeAll(snapshotRemovedObjects)
        snapshotRemovedObjects.clear()
    }

    @Override
    void rollbackCalled(UserModel unitOfWork) {
        snapshotRemovedObjects.clear()
        snapshotAddedObjects.clear()
    }

    @Override
    void saveFailed(UserModel unitOfWork) {

    }

    @Override
    void rollbackFailed(UserModel unitOfWork) {
        snapshotRemovedObjects.clear()
        snapshotAddedObjects.clear()
    }

    static class UserSnapshotAwareIterator<E> implements Iterator<E>{

        @Delegate
        private PeekingIterator<E> iterator
        private Collection removedObjects

        UserSnapshotAwareIterator(Iterator<E> iterator, Collection removedObjects) {
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
