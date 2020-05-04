package com.vmc.objectMemento

//TODO add tests
class Caretaker {

    private Object anObject
    private Deque<Memento> history = new ArrayDeque<>()

    Caretaker(Object anObject) {
        this.anObject = anObject
    }

    void takeNewSnapshot() {
        history.add(new Memento(anObject))
    }

    void rollbackSnapshot() {
        history.pollLast().restore()
    }

    boolean hasSnapshotHistory(){
        !history.isEmpty()
    }

    boolean hasChangesSinceLastSnapshot(){
        if (history.isEmpty()) return false;
        history.peekLast().hasChanges()
    }
}
