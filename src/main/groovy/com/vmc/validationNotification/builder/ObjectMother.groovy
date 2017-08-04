package com.vmc.validationNotification.builder

import com.github.javafaker.Faker
import com.vmc.payroll.Employee
import com.vmc.payroll.payment.delivery.Mail
import com.vmc.payroll.payment.type.Monthly
import com.vmc.validationNotification.Validate
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FirstParam
//todo document
//todo more tests
//switch cglib for byte budy
/**
 *
 */
class ObjectMother<E> {

    protected Class<E> childClass
    protected List<Closure> birthScripts = new ArrayList<Closure>()
    protected Closure postBirthScript = {}

    //for reflection magic
    ObjectMother() {}

    ObjectMother(Class<E> aClass, Closure postBirthScript) {
        this(aClass)
        this.postBirthScript = postBirthScript? postBirthScript : {}
    }


    ObjectMother(Class<E> aClass) {
        if(aClass == null ) throw new IllegalArgumentException("A class to build must be provided")
        this.childClass = aClass
    }

    E addBirthScript(@DelegatesTo(genericTypeIndex = 0, strategy = Closure.DELEGATE_FIRST)
                     @ClosureParams(FirstParam.FirstGenericType)
                     Closure<E> birthScript) {
        birthScripts.add(birthScript)
    }

    E createNewBornWithScript(@DelegatesTo(genericTypeIndex = 0, strategy = Closure.DELEGATE_FIRST)
                              @ClosureParams(FirstParam.FirstGenericType)
                              Closure<E> ...birthScript) {
        return createNewBorn(birthScript as List)
    }

    E createNewBorn() {
        return createNewBorn(birthScripts)
    }

    E createNewBorn(List<Closure> birthScripts){
        return Validate.validate({
            def newBornChild = childClass.newInstance()
            birthScripts.each {birthScript -> newBornChild .with birthScript}
            return newBornChild
        }).onBuildSucess(postBirthScript)
    }

    static void main(String[] args) {
        ObjectMother<Employee> employeeMother = new ObjectMother<Employee>(Employee, { Employee emp ->
            print(emp.name + ", ")
        })

        def faker = new Faker(new Locale("pt-BR"))
        employeeMother.addBirthScript {
            setName({faker.name().firstName()}())
            setAddress({faker.address().streetAddress()}())
            setEmail("teste@bla.com")
            bePaid({Monthly.newPaymentType(it, 2000)})
            receivePaymentBy({Mail.newPaymentDelivery(it, "Street 1")})
        }


        (1..100).each {
            employeeMother.createNewBorn()
        }
    }
}