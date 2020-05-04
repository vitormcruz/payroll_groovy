package com.vmc.objectMemento


import java.time.LocalDateTime

//TODO add tests
class Memento {

    private Object anObject
    final String objectState
    final LocalDateTime snapshotDate
    private byte[] objectStateId

    Memento(Object anObject) {
        this.anObject = anObject
        objectState = anObject.myState()
        snapshotDate = LocalDateTime.now()
        objectStateId = anObject.getCurrentObjectStateId()
    }

    boolean hasChanges(){
        return objectStateId != anObject.getCurrentObjectStateId()
    }

    def restore(){
        anObject.restoreState(this)
    }

}
