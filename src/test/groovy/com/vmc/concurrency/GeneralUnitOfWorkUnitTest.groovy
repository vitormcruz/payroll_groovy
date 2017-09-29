package com.vmc.concurrency

import com.github.javafaker.Faker
import org.junit.Test

class GeneralUnitOfWorkUnitTest {

    protected static faker =  new Faker(new Locale("pt-BR"))
    public static final int TIMEOUT = 2000

    @Test
    void "Test obtaining used object from user model"(){
        def unitOfWork = new GeneralUnitOfWork()
        def date = unitOfWork.addToUserModel(new Date())
        System.gc()
        assertWaitingSuccess({unitOfWork.getUserModel().contains(date)})
    }

    @Test
    void "Test obtaining unused and unchanged object from user model"(){
        def unitOfWork = new GeneralUnitOfWork()
        unitOfWork.addToUserModel(new Date())
        System.gc()
        assertWaitingSuccess({assert unitOfWork.getUserModel().isEmpty()})
    }

    def assertWaitingSuccess(assertion, timeout=TIMEOUT, originalError=null){
        try {
            sleep(100)
            timeout -= 100
            assertion()
        } catch (AssertionError e) {
            if(timeout < 0) throw originalError? originalError : e
            assertWaitingSuccess(assertion, timeout, originalError? originalError : e)
        }


    }

//        def employeeMother = new ObjectMother<Employee>(Employee).addBirthScript {
      //                                    setName(faker.name().firstName())
      //                                    setAddress(faker.address().streetAddress())
      //                                    setEmail("${faker.name().nameWithMiddle()}@bla.com.br")
      //                                    bePaid(EmployeePerformanceTest.getRandomPaymentTypeProvider())
      //                                    receivePaymentBy(EmployeePerformanceTest.getRandomPaymentDelivery())
      //                                }
}
