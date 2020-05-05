package com.vmc.validationNotification

import static GenericNullObjectBuilder.failIfCantCreateNullObject
import static GenericNullObjectBuilder.newNullObjectOf

class Validation extends SimpleValidationObserver {

    /**
     * Validates a new object construction made by the aClosure parameter. Returns a generated NullObject subtype of
     * classValidated if validation fail, or the object returned by aClosure, which must return an object of the
     * classValidated type.
     */
    static <R> R validateNewObject(Class<R> classValidated, Closure<R> aClosure) {
        failIfCantCreateNullObject(classValidated)
        def observer = new SimpleValidationObserver()
        ApplicationValidationNotifier.addObserver(observer)
        def result = executeIgnoringValidationException(aClosure)
        ApplicationValidationNotifier.removeObserver(observer)
        return observer.successful() ? result : newNullObjectOf(classValidated, observer.errorsByContext)
    }

    static executeIgnoringValidationException(Closure aClosureToValidate) {
        try {
            return aClosureToValidate()
        } catch (ValidationFailedException e) {
            //Ignored. Validation failure will be treated differently.
        }
    }

    /**
     * Creates a validation object that fails with a ValidationFailedException if an error is notified when the provided closure is executed. The thrown execption has all errors
     * concatenated in it's message.
     */
    static validate(Closure<Void> aClosureToValidate) {
        def observer = new SimpleValidationObserver()
        ApplicationValidationNotifier.addObserver(observer)
        aClosureToValidate()
        ApplicationValidationNotifier.removeObserver(observer)
        if(!observer.successful()) throw new ValidationFailedException(observer.getCommaSeparatedErrors())
    }
}
