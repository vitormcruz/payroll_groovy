package sandbox.payroll.interfaceAdapter.persistence.hibernate

import sandbox.concurrency.AtomicBlock
import sandbox.payroll.ModelSnapshot

class HibernatePersistentModelSnapshot implements ModelSnapshot{

    private AtomicBlock atomicBlock = AtomicBlock.smartNewFor(HibernatePersistentModelSnapshot)
    private modelObjects = []

    @Override
    def void save() {
        atomicBlock.execute{
            modelObjects.each { it.executeAllPending()}
        }
    }

    @Override
    void add(Object modelObject) {
        modelObjects.add(modelObject)
    }
}
