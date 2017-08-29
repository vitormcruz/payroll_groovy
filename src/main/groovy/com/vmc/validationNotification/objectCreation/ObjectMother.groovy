package com.vmc.validationNotification.objectCreation

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FirstParam

import static com.vmc.validationNotification.Validation.validateNewObject

/**
 * I am a generic object mother that can be used also as a generic build. I can only be used to classes that have a default constructor.
 */
class ObjectMother<E> {

    protected Class<E> childClass
    protected List<Closure> birthScripts = new ArrayList<Closure>()
    protected Closure postBirthScript = {}

    //for reflection magic
    ObjectMother() {}

    /**
     * Creates a new ObjectMother for aClass
     */
    ObjectMother(Class<E> aClass) {
        if(aClass == null ) throw new IllegalArgumentException("A class to build must be provided")
        this.childClass = aClass
    }

    ObjectMother(Class<E> childClass, List<Closure> birthScripts, Closure postBirthScript) {
        this(childClass)
        this.birthScripts = birthScripts
        this.postBirthScript = postBirthScript? postBirthScript : {}
    }

    /**
     * Return a clone of this ObjectMother changing the birth script to the one represented by the aPostBirthScript closure. This script will be executed right after a new born
     * object is created, which will be provided as the script the sole parameter.
     */
    ObjectMother<E> configurePostBirthScript(@ClosureParams(FirstParam.FirstGenericType)
                                             Closure<E> aPostBirthScript) {

        return new ObjectMother<E>(childClass, birthScripts, aPostBirthScript)
    }


    /**
     * Return a clone of this ObjectMother adding a new birth script. All birth scripts will be executed, in the order of it's addition, as part of the new born creationg
     * procedure, and all of them will receive the instance of the new born to be configured appropriately.
     */
    ObjectMother<E> addBirthScript(@DelegatesTo(genericTypeIndex = 0, strategy = Closure.DELEGATE_FIRST)
                     @ClosureParams(FirstParam.FirstGenericType)
                     Closure<E> birthScript) {

        return new ObjectMother<E>(childClass, [birthScripts, [birthScript]].collectMany {it}, postBirthScript)
    }

    /**
     * Does the same of createNewBorn, but ignoring all the configured birth scripts, using instead those provided as parameter. This method can be used as a generic builder,
     * since all methods from the built object can be called directly from the closures, i.e. there is no need to use the closure parameter 'it'.
     */
    E createNewBornWithScript(@DelegatesTo(genericTypeIndex = 0, strategy = Closure.DELEGATE_FIRST)
                              @ClosureParams(FirstParam.FirstGenericType)
                              Closure<E> ...birthScript) {
        return createNewBorn(birthScript as List)
    }

    /**
     * Creates a new born passing itself to all the configured birth scripts and later, if no error occurs, calling the post birth script also passing the new born to it. It
     * returns the new born object a generic NullObject. This method is really useful to be used with proper ObjectMother configuration so to provide relevant DataSets only using
     * domain logic, it is especially usefull if used alongside with APIs such as faker and FixtitureFactory.
     */
    E createNewBorn() {
        return createNewBorn(birthScripts)
    }

    E createNewBorn(List<Closure> birthScripts){
        return validateNewObject(childClass, {
            def newBornChild = childClass.newInstance()
            birthScripts.each {birthScript -> newBornChild.with birthScript}
            return newBornChild
        }).onBuildSuccess(postBirthScript)
    }
}