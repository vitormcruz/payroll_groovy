package com.vmc.payroll

import com.github.javafaker.Faker
import com.vmc.payroll.api.EmployeeRepository
import com.vmc.payroll.external.config.ServiceLocator
import com.vmc.payroll.payment.delivery.AccountTransfer
import com.vmc.payroll.payment.delivery.Mail
import com.vmc.payroll.payment.delivery.Paymaster
import com.vmc.payroll.payment.delivery.api.PaymentDelivery
import com.vmc.payroll.payment.type.Commission
import com.vmc.payroll.payment.type.Hourly
import com.vmc.payroll.payment.type.Monthly
import com.vmc.payroll.payment.type.GenericPaymentType
import com.vmc.payroll.testPreparation.IntegrationTestBase
import com.vmc.validationNotification.builder.ObjectMother
import org.junit.BeforeClass
import org.junit.Test

class EmployeePerformanceTest extends IntegrationTestBase {

    private static EmployeeRepository employeeRepository = ServiceLocator.instance.employeeRepository()
    private static faker =  new Faker(new Locale("pt-BR"))
    private static random = new Random()

    private static List<Closure<GenericPaymentType>> paymentTypesProviders =
            [{Monthly.newPaymentType(it, faker.number().numberBetween(1000, 10000)) },
             {Hourly.newPaymentType(it, faker.number().numberBetween(10, 500)) },
             {Commission.newPaymentType(it, faker.number().numberBetween(1000, 10000),
                     faker.number().numberBetween(1, 100))}
            ]

    private static List<Closure<PaymentDelivery>> paymentDeliveriesProviders =
            [{Mail.newPaymentDelivery(it, faker.address().streetAddress()) },
             {Paymaster.newPaymentDelivery(it) },
             {AccountTransfer.newPaymentDelivery(it, faker.finance().iban(), faker.idNumber().valid()) }
            ]

    private static ObjectMother<Employee> employeeMother
    public static final int MAX_TIME_EXECUTION = 5000

    def benchmark = { closure ->
      def start = System.currentTimeMillis()
      closure.call()
      def now = System.currentTimeMillis()
      now - start
    }

    @BeforeClass
    def static void setupAll(){
        employeeMother = new ObjectMother<Employee>(Employee)
                                .setPostBirthScript { newEmployee -> employeeRepository.add(newEmployee)}
                                .addBirthScript {
                                    setName({ faker.name().firstName()}())
                                    setAddress({ faker.address().streetAddress()}())
                                    setEmail({"${faker.name().nameWithMiddle()}@bla.com.br"}())
                                    bePaid({getRandomPaymentTypeProvider()}())
                                    receivePaymentBy({getRandomPaymentDelivery()}())
                                }

    }

    @Test
    void "Insert lots of different employees"(){
        assert benchmark {1000.times {employeeMother.createNewBorn()}} < MAX_TIME_EXECUTION
    }

    static getRandomPaymentTypeProvider() {
        return paymentTypesProviders.get(random.nextInt(3))
    }

    static getRandomPaymentDelivery() {
        return paymentDeliveriesProviders.get(random.nextInt(3))
    }

}
