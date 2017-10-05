package com.vmc.concurrency

class GeneralUnitOfWork {

    protected Map<TrackObject, Closure> userModel = Collections.synchronizedMap(new HashMap<TrackObject, Closure>())

    def <E> E addToUserModel(E entity, syncronizeObjectClosure) {
        if(!(entity instanceof Serializable)) return entity
        def trackedObject = TrackObject.newTrackedObject(entity, { removeUnusedObjectIfNotDirty(it) })
        userModel.put(trackedObject, syncronizeObjectClosure)
        return trackedObject.newTrackingProxyForMe()
    }

    void removeUnusedObjectIfNotDirty(TrackObject unusedObject){
        if(unusedObject.isNotDirty()){
            this.@userModel.remove(unusedObject)
        }
    }

    Set getUserModel() {
       return new HashSet(this.@userModel.keySet().collect({it.getTrackedObject()}))
    }

    void save() {
        userModel.entrySet().each {
            def trackObject = it.key
            def syncronizeObjectClosure = it.value
            if(trackObject.isDirty()){
                syncronizeObjectClosure(trackObject.trackedObject)
            }
        }
    }
}
