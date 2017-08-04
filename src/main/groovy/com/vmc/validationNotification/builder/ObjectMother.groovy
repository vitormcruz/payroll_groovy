package com.vmc.validationNotification.builder

import com.github.javafaker.Faker
import com.vmc.payroll.Employee
import com.vmc.payroll.payment.delivery.Mail
import com.vmc.payroll.payment.type.Monthly
import com.vmc.validationNotification.Validate
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FirstParam
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.Factory
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy

import java.lang.reflect.Method

//todo document
//todo more tests
//switch cglib for byte budy
/**
 *
 */
class ObjectMother<E> {

    protected WeakHashMap<E, Map<String, Object[]>> messagesCall = new HashMap<String, Object[]>()
    protected Map providers = [:]
    protected Class<E> childClass
    protected Closure postBornScript = {}
    protected E embryo
    protected Factory embryoFactory
    protected Collection<String> methodsToRecord

    //for reflection magic
    ObjectMother() {}

    ObjectMother(Class<E> aClass, Closure postBornScript) {
        this(aClass)
        this.postBornScript = postBornScript? postBornScript : {}
    }


    ObjectMother(Class<E> aClass) {
        if(aClass == null ) throw new IllegalArgumentException("A class to build must be provided")
        this.childClass = aClass
        methodsToRecord = childClass.declaredMethodsFromMyInheritanceTree()
        embryoFactory = createEmbryoCGFactory(methodsToRecord)
        embryo = embryoFactory
        messagesCall.put(embryo, [:])
    }

    E createEmbryoCGFactory(aListMethodsToRecord) {
        return Enhancer.create(childClass, { Object obj, Method method, Object[] args, MethodProxy proxy ->
            if (aListMethodsToRecord.contains(method.name)) {
                messagesCall.get(obj).put(method.name, args)
            } else {
                proxy.invokeSuper(obj, args)
            }
        } as MethodInterceptor)
    }

    E getEmbryo() {
        return embryo
    }

    E createNewBornWithEmbryoConfig(@DelegatesTo(genericTypeIndex = 0, strategy = Closure.DELEGATE_FIRST)
                                    @ClosureParams(FirstParam.FirstGenericType)
                                    Closure<E> embryoConfiguration) {
        def newEmbryo = createNewEmbryo()
        newEmbryo.with embryoConfiguration
        return createNewBorn(newEmbryo)
    }

    E createNewEmbryo() {
        def newEmbryo = embryoFactory.newInstance(embryoFactory.getCallbacks())
        messagesCall.put(newEmbryo, [:])
        return newEmbryo
    }

    E createNewBorn() {
        return createNewBorn(embryo)
    }

    E createNewBorn(E anEmbryo){
        return Validate.validate({
            def newBornChild = childClass.newInstance()
            messagesCall.get(anEmbryo).each { String name, args ->
                newBornChild."${name}"(*args.collect { field -> providers.get(field) ? providers.get(field)() : field })
            }
            return newBornChild
        }).onBuildSucess(postBornScript)
    }

    def setProvider(providerKey, Closure provider) {
        providers.put(providerKey, provider)
    }

    static void main(String[] args) {
        ObjectMother<Employee> employeeMother = new ObjectMother<Employee>(Employee, { Employee emp ->
//            print(emp.name)
        })
        def faker = new Faker(new Locale("pt-BR"))

        (1..1000000).each {
            if(it % 1000 == 0){
                println(employeeMother.messagesCall.size())
            }
            employeeMother.createNewBornWithEmbryoConfig {
                setName(employeeMother.provider {faker.name().firstName()} )
                setAddress(employeeMother.provider {faker.address().streetAddress()} )
                setEmail("teste@bla.com")
                bePaid({Monthly.newPaymentType(it, 2000)})
                receivePaymentBy({Mail.newPaymentDelivery(it, "Street 1")})
            }

        }
    }

    public <P> P provider(Closure<P> provider) {
        def providerKey = provider()
        this.setProvider(providerKey, provider)
        return providerKey
    }
}