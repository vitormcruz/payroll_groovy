package com.vmc.concurrency.extensions.instance.java.lang

import com.cedarsoftware.util.io.JsonReader
import com.cedarsoftware.util.io.JsonWriter
import com.vmc.concurrency.memento.Caretaker
import com.vmc.concurrency.memento.Memento
import org.modelmapper.ModelMapper
import org.modelmapper.config.Configuration

import java.security.MessageDigest

import static java.util.Optional.ofNullable

class ObjectMementoExtensions {

    static private WeakHashMap<Object, Caretaker> caretakerOfObject = new WeakHashMap()
    static private ModelMapper modelMapper = new ModelMapper();

    static {
        modelMapper.getConfiguration().setMethodAccessLevel(Configuration.AccessLevel.PRIVATE)
                                      .setFieldMatchingEnabled(true)
    }

    static void takeSnapshot(anObject){
        if(anObject instanceof Class) throw new UnsupportedOperationException("Cannot make a memento of a class")
        caretakerOfObject.putIfAbsent(anObject, new Caretaker(anObject))
        Caretaker caretaker = caretakerOfObject.get(anObject)
        caretaker.takeNewSnapshot()
    }

    static <T> boolean hasSnapshotHistory(T object){
        return caretakerOfObject.get(object) != null
    }

    static void rollback(anObject) {
        Caretaker caretaker = caretakerOfObject.get(anObject)
        if(caretaker == null) return
        caretaker.rollbackSnapshot()
        if(!caretaker.hasSnapshotHistory()){
            caretakerOfObject.remove(anObject)
        }
    }

    /**
     * Return true if the memento object was changed. If the object is not a memento, aways return true.
     */
    static boolean isDirty(anObject) {
        return ofNullable(caretakerOfObject.get(anObject)).map { it.hasChangesSinceLastSnapshot() }
                                                          .orElse(false)
    }

    /**
     * The defautl implementation generates MD5 from a simple json generated from object.
     * Override this method to specialize, but take care to change myState method  accordingly since they both
     * work together.
     */
    static getCurrentObjectStateId(anObject) {
        return MessageDigest.getInstance("MD5").digest(anObject.myState().getBytes("UTF-8"))
    }

    /**
     * The default implementation makes return a json represetation of myself. Override this method to specialize.
     */
    static def myState(anObject){
        return JsonWriter.objectToJson(anObject)
    }

    static void restoreState(anObject, Memento myMementoObject){
        modelMapper.map(JsonReader.jsonToJava(myMementoObject.objectState), anObject)
    }

}

