package com.vmc.payroll.external.persistence.orientDB.repository

import com.google.common.collect.testing.CollectionTestSuiteBuilder
import com.vmc.payroll.domain.api.Repository
import com.vmc.payroll.external.config.ServiceLocator
import com.vmc.payroll.testPreparation.IntegrationTestBase
import junit.framework.Test
import junit.framework.TestSuite
import org.junit.Ignore
import org.junit.runner.RunWith
import org.junit.runners.AllTests

import static com.google.common.collect.testing.features.CollectionFeature.*
import static com.google.common.collect.testing.features.CollectionSize.ANY

@Ignore("Incomplete")
@RunWith(AllTests)
class OrientDBRepositoryIntTest extends IntegrationTestBase{
    private OrientDBRepository<FakeEntity> dummyRepository = new OrientDBRepository(FakeEntity, ServiceLocator.instance.database)

    public static Test suite() {
      return new OrientDBRepositoryIntTest().allTests()
    }

    public Test allTests() {
      TestSuite suite = new TestSuite("Repository Tests")
        suite.addTest(CollectionTestSuiteBuilder.using(
                new TestRepositoryFakeEntityGenerator({getNewRepositoryInstance()}))
                .named("General Repository Test")
                .withFeatures(ANY, GENERAL_PURPOSE, SUBSET_VIEW, DESCENDING_VIEW)
                .withSetUp({ dummyRepository.clear() })
                .createTestSuite())
      return suite
    }

    Repository<FakeEntity> getNewRepositoryInstance() {
        return new OrientDBRepository(FakeEntity, ServiceLocator.instance.database)
    }
}
