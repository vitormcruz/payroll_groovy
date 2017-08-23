package com.vmc.validationNotification.objectCreation

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FirstParam

import static com.vmc.validationNotification.Validate.validateNewObject
//todo document
//todo more tests
/**
 *
 */
class ObjectMother<E> {

    protected Class<E> childClass
    protected List<Closure> birthScripts = new ArrayList<Closure>()
    protected Closure postBirthScript = {}

    //for reflection magic
    ObjectMother() {}

    ObjectMother(Class<E> aClass) {
        if(aClass == null ) throw new IllegalArgumentException("A class to build must be provided")
        this.childClass = aClass
    }

    ObjectMother(Class<E> childClass, List<Closure> birthScripts, Closure postBirthScript) {
        this(childClass)
        this.birthScripts = birthScripts
        this.postBirthScript = postBirthScript? postBirthScript : {}
    }

    ObjectMother<E> configurePostBirthScript(@ClosureParams(FirstParam.FirstGenericType)
                                             Closure<E> aPostBirthScript) {

        return new ObjectMother<E>(childClass, birthScripts, aPostBirthScript)
    }


    ObjectMother<E> addBirthScript(@DelegatesTo(genericTypeIndex = 0, strategy = Closure.DELEGATE_FIRST)
                     @ClosureParams(FirstParam.FirstGenericType)
                     Closure<E> birthScript) {

        return new ObjectMother<E>(childClass, [birthScripts, [birthScript]].collectMany {it}, postBirthScript)
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
        return validateNewObject(childClass, {
            def newBornChild = childClass.newInstance()
            birthScripts.each {birthScript -> newBornChild .with birthScript}
            return newBornChild
        }).onBuildSuccess(postBirthScript)
    }
}