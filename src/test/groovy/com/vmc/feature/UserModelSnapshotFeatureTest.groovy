package com.vmc.feature

import com.vmc.concurrency.GeneralUserModelSnapshot
import com.vmc.concurrency.UserSnapshotAwareRepository
import com.vmc.concurrency.api.UserModelSnapshot
import com.vmc.payroll.domain.api.Entity
import com.vmc.payroll.external.persistence.inMemory.repository.CommonInMemoryRepositoryVersion2
import org.junit.BeforeClass
import org.junit.Test

import java.security.MessageDigest

class UserModelSnapshotFeatureTest {

    static private UserModelSnapshot userModelSnapshot
    static private MessageDigest md5Digester

    @BeforeClass
    static void setUpAll() throws Exception {
        userModelSnapshot = new GeneralUserModelSnapshot()
        UserModelSnapshot.load(userModelSnapshot)
        md5Digester = MessageDigest.getInstance("MD5")
    }

    //
    // Common scenarios. Look at this if you wanna understand the basic bahavior of repository, entities and userModelSnapshot.
    //

    @Test
    void "Add a new entity into a repository and don't save the model snapshot"(){
        def objectRepository = new UserSnapshotAwareRepository<DummyEntity>(new CommonInMemoryRepositoryVersion2<DummyEntity>());
        objectRepository.add(new DummyEntity("test"))
        assert !objectRepository.isEmpty()
        assert ["test"] as Set == objectRepository.collect {it.getId()} as Set
        assert objectRepository.savedObjects().isEmpty()
    }

    @Test
    void "Add a new entity into a repository and save the model snapshot"(){
        def objectRepository = new UserSnapshotAwareRepository<DummyEntity>(new CommonInMemoryRepositoryVersion2<DummyEntity>());
        objectRepository.add(new DummyEntity("test"))
        userModelSnapshot.save()
        assert ["test"] as Set == objectRepository.collect {it.getId()} as Set
        assert ["test"] as Set == objectRepository.savedObjects().collect {it.getId()} as Set
    }

    @Test
    void "Add a new entity into a repository but rollback the model snapshot"(){
        def objectRepository = new UserSnapshotAwareRepository<DummyEntity>(new CommonInMemoryRepositoryVersion2<DummyEntity>());
        objectRepository.add(new DummyEntity("test"))
        userModelSnapshot.rollback()
        assert objectRepository.isEmpty()
        assert objectRepository.savedObjects().isEmpty()
    }

    @Test
    void "Remove an entity from a repository and don't save the model snapshot"(){
        def objectToRemove = new DummyEntity("test")
        UserSnapshotAwareRepository<Entity> objectRepository = createRepositoryWith(objectToRemove)
        objectRepository.remove(objectToRemove)
        assert objectRepository.isEmpty()
        assert ["test"] as Set == objectRepository.savedObjects().collect {it.getId()} as Set
    }

    @Test
    void "Remove an entity from a repository and save the model snapshot"(){
        def objectToRemove = new DummyEntity("test")
        UserSnapshotAwareRepository<Entity> objectRepository = createRepositoryWith(objectToRemove)
        objectRepository.remove(objectToRemove)
        userModelSnapshot.save()
        assert objectRepository.isEmpty()
        assert objectRepository.savedObjects().isEmpty()
    }

    @Test
    void "Remove an entity from a repository, but rollback the model snapshot"(){
        def objectToRemove = new DummyEntity("test")
        UserSnapshotAwareRepository<Entity> objectRepository = createRepositoryWith(objectToRemove)
        objectRepository.remove(objectToRemove)
        userModelSnapshot.rollback()
        assert ["test"] as Set == objectRepository.collect {it.getId()} as Set
        assert ["test"] as Set == objectRepository.savedObjects().collect {it.getId()} as Set
    }

    @Test
    void "Change an entiy obtained from a repository, but rollback the model snapshot"(){
        UserSnapshotAwareRepository<Entity> objectRepository = createRepositoryWith(new DummyEntity("test"))
        def objectToChange = objectRepository.get("test")
        objectToChange.field1 = "Changed"
        userModelSnapshot.rollback()
        assert objectToChange.field1 == "test"
    }

    //
    // Uncommon egde scenarios
    //

    @Test
    void "Add a new entity into a repository that already contains it and don't save the model snapshot"(){
        def objectRepository = new UserSnapshotAwareRepository<DummyEntity>(new CommonInMemoryRepositoryVersion2<DummyEntity>());
        def addedObject = new DummyEntity("test")
        objectRepository.add(addedObject)
        objectRepository.add(addedObject)
        assert ["test"] as Set == objectRepository.collect {it.getId()} as Set
        assert objectRepository.savedObjects().isEmpty()
    }

    @Test
    void "Add a new entity into a repository that already contains it and save the model snapshot"(){
        def objectRepository = new UserSnapshotAwareRepository<DummyEntity>(new CommonInMemoryRepositoryVersion2<DummyEntity>());
        def addedObject = new DummyEntity("test")
        objectRepository.add(addedObject)
        userModelSnapshot.save()
        objectRepository.add(addedObject)
        assert ["test"] as Set == objectRepository.collect {it.getId()} as Set
        assert ["test"] as Set == objectRepository.savedObjects().collect {it.getId()} as Set
    }


    //Must have error handling in notification also

    //If save is used always, the UserSnapshotAwareRepository should behave like any collection ** Add Tests to guarantee this **


    UserSnapshotAwareRepository<DummyEntity> createRepositoryWith(Entity... entity) {
        def objectRepository = new UserSnapshotAwareRepository<Entity>(new CommonInMemoryRepositoryVersion2<Entity>());
        objectRepository.addAll(entity)
        userModelSnapshot.save()
        return objectRepository
    }
}