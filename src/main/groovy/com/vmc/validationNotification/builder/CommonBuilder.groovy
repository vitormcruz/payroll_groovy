package com.vmc.validationNotification.builder

/**
 * Builder with common and useful protocols to deal with success and failure scenarios
 */
interface CommonBuilder {

    /**
     * Build the object and execute aSuccessClosure upon success. Return the built object or null in case of failure.
     */
    def buildAndDoOnSuccess(aSuccessClosure)

    /**
     * Build the object and execute aSuccessClosure upon success or aFailureClosure otherwise. Return the built object
     * or null in case of failure.
     */
    def buildAndDo(aSuccessClosure, aFailureClosure)

    /**
     * Build the object and execute aFailureClosure upon failure. Return the built object or null in case of failure.
     */
    def buildAndDoOnFailure(aFailureClosure)

    /**
     * Return the successfully built object or null otherwise
     */
    def build()
}