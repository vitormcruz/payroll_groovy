package com.vmc.concurrency.extensions.instance.java.lang

import com.google.gson.Gson
import com.rits.cloning.Cloner
import org.modelmapper.ModelMapper
import org.modelmapper.config.Configuration

import java.security.MessageDigest

class ObjectMementoExtensions {

    static private WeakHashMap<Object, String> stateIds = new WeakHashMap()
    static private WeakHashMap<Object, Object> originOfMemento = new WeakHashMap()
    static private ModelMapper modelMapper = new ModelMapper();
    static private Cloner cloner = new Cloner()
    static private Gson gson = new Gson()

    static {
        modelMapper.getConfiguration().setMethodAccessLevel(Configuration.AccessLevel.PRIVATE)
                                      .setFieldMatchingEnabled(true)
    }

    static <T> T getMemento(T originObject){
        if(originObject instanceof Class) throw new UnsupportedOperationException("Cannot make a memento of a class")
        def memento = originObject.createObjectWithMyState()
        originOfMemento.put(memento, originObject)
        stateIds.put(memento, memento.getCurrentObjectStateId())
        return memento
    }

    /**
     * The defautl implementation makes a deepCopy, override this method to specialize.
     */
    static def createObjectWithMyState(anObject){
        return cloner.deepClone(anObject)
    }

    static <T> T rollback(T  mementoObject) {
        def originObject = originOfMemento.get(mementoObject)
        if(originObject == null) return mementoObject
        modelMapper.map(originObject, mementoObject)
        return mementoObject
    }

    /**
     * Return true if the memento object was changed. If the object is not a memento, aways return true.
     */
    static boolean isDirty(object) {
        def mementoState = stateIds.get(object)
        return mementoState ? object.getCurrentObjectStateId() != mementoState : true

    }

    /**
     * The defautl implementation generates MD5 from a simple json generated from object, override this method to specialize.
     */
    static getCurrentObjectStateId(object) {
        return MessageDigest.getInstance("MD5").digest(object.getRawState())
    }

    /**
     * The defautl implementation returns a json representation of the state, override this method to specialize.
     */
    static byte[] getRawState(anObject) {
        return gson.toJson(anObject).getBytes("UTF-8")
    }


}

