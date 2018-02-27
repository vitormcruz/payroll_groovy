package com.vmc.feature

import com.vmc.concurrency.GeneralUserModelSnapshot
import com.vmc.concurrency.UserSnapshotAwareRepository
import com.vmc.concurrency.api.UserModelSnapshot
import com.vmc.payroll.domain.api.Entity
import com.vmc.payroll.external.persistence.inMemory.repository.CommonInMemoryRepositoryVersion2
import org.junit.BeforeClass
import org.junit.Test

class UserModelSnapshotFeatureTest {

    static UserModelSnapshot userModelSnapshot

    @BeforeClass
    static void setUpAll() throws Exception {
        userModelSnapshot = new GeneralUserModelSnapshot()
        UserModelSnapshot.load(userModelSnapshot)
    }

    @Test
    void "Add a new object into a repository and don't save the model snapshot"(){
        def objectRepository = new UserSnapshotAwareRepository<DummyEntity>(new CommonInMemoryRepositoryVersion2<DummyEntity>());
        objectRepository.add(new DummyEntity("test"))
        assert ["test"] as Set == objectRepository.collect {it.getId()} as Set
        assert objectRepository.savedObjects().isEmpty()
    }

    @Test
    void "Add a new object into a repository and save the model snapshot"(){
        def objectRepository = new UserSnapshotAwareRepository<DummyEntity>(new CommonInMemoryRepositoryVersion2<DummyEntity>());
        objectRepository.add(new DummyEntity("test"))
        userModelSnapshot.save()
        assert ["test"] as Set == objectRepository.collect {it.getId()} as Set
        assert ["test"] as Set == objectRepository.savedObjects().collect {it.getId()} as Set
    }

    @Test
    void "Add a new object into a repository that already contains it and don't save the model snapshot"(){

    }

    @Test
    void "Add a new object into a repository that already contains it and save the model snapshot"(){

    }

    @Test
    void "Create a new object and save the model snapshot"(){
        def modelSnapshot = new GeneralUserModelSnapshot()
        def objectRepository = new UserSnapshotAwareRepository<DummyEntity>(modelSnapshot, new CommonInMemoryRepositoryVersion2<DummyEntity>());
        objectRepository.add(new DummyEntity("test"))
        modelSnapshot.save()
        assert ["test"] as Set == objectRepository.collect {it.getId()} as Set
    }

    @Test
    void "Create a new object but rollback the model snapshot"(){
        def snapshotUnitOfWork = new GeneralUserModelSnapshot()
        def objectRepository = new HashSet()
        objectRepository.add(new Date(1))
        snapshotUnitOfWork.save()


    }

    static class DummyEntity implements Entity{

        private String id

        DummyEntity(String id) {
            this.id = id
        }

        @Override
        def getId() {
            return id
        }
    }
}
