package com.vmc.userModel.feature

import com.vmc.objectMemento.InMemoryObjectChangeProvider
import com.vmc.payroll.domain.api.Entity
import com.vmc.payroll.external.persistence.inMemory.repository.CommonInMemoryRepositoryVersion2
import com.vmc.userModel.DummyEntity
import com.vmc.userModel.GeneralUserModel
import com.vmc.userModel.UserModelAwareRepository
import com.vmc.userModel.api.UserModel
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import java.security.MessageDigest

class UserModelFeatureTest {

    static private UserModel userModelSnapshot
    static private MessageDigest md5Digester

    @BeforeAll
    static void setUpAll() throws Exception {
        userModelSnapshot = new GeneralUserModel()
        UserModel.load(userModelSnapshot)
        md5Digester = MessageDigest.getInstance("MD5")
    }

    //
    // Common scenarios. Look at this if you wanna understand the basic behavior of repository, entities and userModelSnapshot.
    //

    @Test
    void "Add a new entity into a repository and don't save the model snapshot"(){
        def objectRepository = new UserModelAwareRepository<DummyEntity>(new CommonInMemoryRepositoryVersion2<DummyEntity>(), userModelSnapshot, new InMemoryObjectChangeProvider());
        objectRepository.add(new DummyEntity("test"))
        assert !objectRepository.isEmpty()
        assert ["test"] as Set == objectRepository.collect {it.getId()} as Set
        assert objectRepository.savedObjects().isEmpty()
    }

    @Test
    void "Add a new entity into a repository and save the model snapshot"(){
        def objectRepository = new UserModelAwareRepository<DummyEntity>(new CommonInMemoryRepositoryVersion2<DummyEntity>(), userModelSnapshot, new InMemoryObjectChangeProvider());
        objectRepository.add(new DummyEntity("test"))
        userModelSnapshot.save()
        assert ["test"] as Set == objectRepository.collect {it.getId()} as Set
        assert ["test"] as Set == objectRepository.savedObjects().collect {it.getId()} as Set
    }

    @Test
    void "Add a new entity into a repository but rollback the model snapshot"(){
        def objectRepository = new UserModelAwareRepository<DummyEntity>(new CommonInMemoryRepositoryVersion2<DummyEntity>(), userModelSnapshot, new InMemoryObjectChangeProvider());
        objectRepository.add(new DummyEntity("test"))
        userModelSnapshot.rollback()
        assert objectRepository.isEmpty()
        assert objectRepository.savedObjects().isEmpty()
    }

    @Test
    void "Remove an entity from a repository and don't save the model snapshot"(){
        def objectToRemove = new DummyEntity("test")
        UserModelAwareRepository<Entity> objectRepository = createRepositoryWith(objectToRemove)
        objectRepository.remove(objectToRemove)
        assert objectRepository.isEmpty()
        assert ["test"] as Set == objectRepository.savedObjects().collect {it.getId()} as Set
    }

    @Test
    void "Remove an entity from a repository and save the model snapshot"(){
        def objectToRemove = new DummyEntity("test")
        UserModelAwareRepository<Entity> objectRepository = createRepositoryWith(objectToRemove)
        objectRepository.remove(objectToRemove)
        userModelSnapshot.save()
        assert objectRepository.isEmpty()
        assert objectRepository.savedObjects().isEmpty()
    }

    @Test
    void "Remove an entity from a repository, but rollback the model snapshot"(){
        def objectToRemove = new DummyEntity("test")
        UserModelAwareRepository<Entity> objectRepository = createRepositoryWith(objectToRemove)
        objectRepository.remove(objectToRemove)
        userModelSnapshot.rollback()
        assert ["test"] as Set == objectRepository.collect {it.getId()} as Set
        assert ["test"] as Set == objectRepository.savedObjects().collect {it.getId()} as Set
    }

    @Test
    void "Change an entiy obtained from a repository, but rollback the model snapshot"(){
        UserModelAwareRepository<Entity> objectRepository = createRepositoryWith(new DummyEntity("id", "test"))
        def objectToChange = objectRepository.get("id")
        objectToChange.field1 = "Changed"
        userModelSnapshot.rollback()
        assert objectToChange.field1 == "test"
    }

    //
    // Uncommon egde scenarios
    //

    @Test
    void "Add a new entity into a repository that already contains it and don't save the model snapshot"(){
        def objectRepository = new UserModelAwareRepository<DummyEntity>(new CommonInMemoryRepositoryVersion2<DummyEntity>(), userModelSnapshot, new InMemoryObjectChangeProvider());
        def addedObject = new DummyEntity("test")
        objectRepository.add(addedObject)
        objectRepository.add(addedObject)
        assert ["test"] as Set == objectRepository.collect {it.getId()} as Set
        assert objectRepository.savedObjects().isEmpty()
    }

    @Test
    void "Add a new entity into a repository that already contains it and save the model snapshot"(){
        def objectRepository = new UserModelAwareRepository<DummyEntity>(new CommonInMemoryRepositoryVersion2<DummyEntity>(), userModelSnapshot, new InMemoryObjectChangeProvider());
        def addedObject = new DummyEntity("test")
        objectRepository.add(addedObject)
        userModelSnapshot.save()
        objectRepository.add(addedObject)
        assert ["test"] as Set == objectRepository.collect {it.getId()} as Set
        assert ["test"] as Set == objectRepository.savedObjects().collect {it.getId()} as Set
    }


    //Must have error handling in notification also

    //If save is used always, the UserModelAwareRepository should behave like any collection ** Add Tests to guarantee this **


    UserModelAwareRepository<DummyEntity> createRepositoryWith(Entity... entity) {
        def objectRepository = new UserModelAwareRepository<Entity>(new CommonInMemoryRepositoryVersion2<Entity>(), userModelSnapshot, new InMemoryObjectChangeProvider());
        objectRepository.addAll(entity)
        userModelSnapshot.save()
        return objectRepository
    }
}
