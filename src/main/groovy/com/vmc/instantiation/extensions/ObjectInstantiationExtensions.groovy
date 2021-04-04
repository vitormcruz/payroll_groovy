package com.vmc.instantiation.extensions

/**
 * Enable objects to notify it's creation and/or register as listener of certain classes instantiations.
 */
class ObjectInstantiationExtensions {

    static Map<Class, Set<InstanceCreatedListener>> listenersByClass = [:]

    static void notifyInstanceCreated(Object self, Object newObject){
        listenersByClass.get(newObject.getClass())?.collect {it.instanceCreated(newObject)}
    }

    static void registerInstanceCreatedListener(InstanceCreatedListener listener, Class aClass){
        listenersByClass.computeIfAbsent(aClass, {new HashSet<>()}).add(listener)
    }
}
