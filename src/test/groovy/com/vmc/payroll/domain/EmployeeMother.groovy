package com.vmc.payroll.domain

import com.github.javafaker.Faker
import com.vmc.instantiation.extensions.ObjectMother
import com.vmc.payroll.domain.payment.delivery.AccountTransfer
import com.vmc.payroll.domain.payment.delivery.Mail
import com.vmc.payroll.domain.payment.delivery.Paymaster
import com.vmc.payroll.domain.payment.type.Commission
import com.vmc.payroll.domain.payment.type.Hourly
import com.vmc.payroll.domain.payment.type.Monthly

class EmployeeMother extends ObjectMother<Employee>{

    EmployeeMother() {
        super(Employee)
    }

    static ObjectMother<Employee> randomEmployeeMother = new EmployeeMother().addBirthScript {
                                                                                                setName(faker.name().firstName())
                                                                                                setAddress(faker.address().streetAddress())
                                                                                                setEmail("${faker.name().nameWithMiddle()}@bla.com.br")
                                                                                                bePaid(getRandomPaymentTypeProvider())
                                                                                                receivePaymentBy(getRandomPaymentDelivery())
                                                                                             }

    private static faker =  new Faker(new Locale("pt-BR"))
    private static random = new Random()
    private static paymentTypesProviders =
                [{ Monthly.newPaymentType(it, faker.number().numberBetween(1000, 10000)) },
                 { Hourly.newPaymentType(it, faker.number().numberBetween(10, 500)) },
                 {
                     Commission.newPaymentType(it, faker.number().numberBetween(1000, 10000),
                             faker.number().numberBetween(1, 100))}
                ]

    private static paymentDeliveriesProviders =
            [{ Mail.newPaymentDelivery(it, faker.address().streetAddress()) },
             { Paymaster.newPaymentDelivery(it) },
             { AccountTransfer.newPaymentDelivery(it, faker.finance().iban(), faker.idNumber().valid()) }
            ]

    static getRandomPaymentTypeProvider() {
        return paymentTypesProviders.get(random.nextInt(3))
    }

    static getRandomPaymentDelivery() {
        return paymentDeliveriesProviders.get(random.nextInt(3))
    }
}
