package com.vmc.payroll.external.persistence.orientDB.repository

import com.google.common.collect.testing.SampleElements
import com.google.common.collect.testing.TestCollectionGenerator
import com.vmc.payroll.domain.api.Repository


class TestRepositoryFakeEntityGenerator implements TestCollectionGenerator<FakeEntity>{

    private Closure<Repository<FakeEntity>> repositoryProvider

    TestRepositoryFakeEntityGenerator(Closure<Repository<FakeEntity>> repositoryProvider) {
        this.repositoryProvider = repositoryProvider
    }

    @Override
    SampleElements<FakeEntity> samples() {
        return new SampleElements<FakeEntity>(new FakeEntity("a"), new FakeEntity("b"), new FakeEntity("ca"),
                                               new FakeEntity("d"), new FakeEntity("ea"))
    }

    @Override
    Repository<FakeEntity> create(Object... elements) {
        def repository = repositoryProvider()
        repository.addAll(elements)
        return repository
    }

    @Override
    FakeEntity[] createArray(int length) {
      return new FakeEntity[length];
    }

    @Override
    Iterable<FakeEntity> order(List<FakeEntity> insertionOrder) {
        def repository = repositoryProvider()
        repository.addAll(insertionOrder)
        return repository
    }

}
