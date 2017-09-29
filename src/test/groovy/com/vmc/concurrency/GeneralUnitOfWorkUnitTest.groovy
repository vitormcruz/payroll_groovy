package com.vmc.concurrency

import com.github.javafaker.Faker
import org.junit.Test

class GeneralUnitOfWorkUnitTest {

    protected static faker =  new Faker(new Locale("pt-BR"))
    public static final int TIMEOUT = 2000

    @Test
    void "Test obtaining used object from user model"(){
        def unitOfWork = new GeneralUnitOfWorkForTest()
        def date = unitOfWork.addToUserModel(new Date())
        System.gc()
        unitOfWork.waitWhileNotifyGcPerformedNotDoneFor(TIMEOUT)
        assert unitOfWork.getUserModel().contains(date)
    }

    @Test
    void "Test obtaining unused and unchanged object from user model"(){
        def unitOfWork = new GeneralUnitOfWorkForTest()
        unitOfWork.addToUserModel(new Date())
        System.gc()
        unitOfWork.waitWhileNotifyGcPerformedNotDoneFor(TIMEOUT)
        assert unitOfWork.getUserModel().isEmpty()
    }

    //TODO test handler is removed from emmiter listener

//        def employeeMother = new ObjectMother<Employee>(Employee).addBirthScript {
      //                                    setName(faker.name().firstName())
      //                                    setAddress(faker.address().streetAddress())
      //                                    setEmail("${faker.name().nameWithMiddle()}@bla.com.br")
      //                                    bePaid(EmployeePerformanceTest.getRandomPaymentTypeProvider())
      //                                    receivePaymentBy(EmployeePerformanceTest.getRandomPaymentDelivery())
      //                                }
}
