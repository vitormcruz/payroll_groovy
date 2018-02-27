package com.vmc.concurrency.defautImpl

import com.rits.cloning.Cloner

class ObjectSnapshotExtensions {

    private static Cloner cloner = new Cloner()
    private static WeakHashMap snapshotOriginField = []

    static <T> T takeSnapshot(T originObject){
        def snapshot = cloner.deepClone(originObject)
        snapshotOriginField.put(snapshot, originObject)
        return snapshot
    }

    static <T> T rollbackSnapshot(T snapshotObject){
        def originObject = snapshotOriginField.get(snapshotObject)
        return originObject ? originObject : snapshotObject as T
    }
}

