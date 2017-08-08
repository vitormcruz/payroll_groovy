package com.vmc.validationNotification

class ObjectValidationNotificationExtensions {

    static void issueError(Object subject, String message){
        ApplicationValidationNotifier.issueError(subject, [:], message)
    }

    static void issueError(Object subject, String instantValidationName, String message){
        ApplicationValidationNotifier.issueError(subject, [:], instantValidationName, message)
    }

    static void issueError(Object subject, String message, Map context){
        ApplicationValidationNotifier.issueError(subject, context, message)
    }

    static void issueError(Object subject, String instantValidationName, String message, Map context){
        ApplicationValidationNotifier.issueError(subject, context, instantValidationName, message)
    }

    static String issueMandatoryObligation(Object subject, String error, Map context) {
        def mandatoryObligationName = UUID.randomUUID().toString()
        ApplicationValidationNotifier.issueMandatoryObligation(subject, context, mandatoryObligationName, error)
        return mandatoryObligationName
    }

    static String issueMandatoryObligation(Object subject, String error) {
        return issueMandatoryObligation(subject, error, [:])
    }

    static void issueMandatoryObligationComplied(Object subject, String mandatoryObligationId, Map context) {
        ApplicationValidationNotifier.issueMandatoryObligationComplied(subject, context, mandatoryObligationId)
    }

    static void issueMandatoryObligationComplied(Object subject, String mandatoryObligationId) {
        issueMandatoryObligationComplied(subject, mandatoryObligationId, [:])
    }

    /**
     * Execute aSuccessClosure if the object building was ok passing myself as parameter.
     */
    static onBuildSucess(Object buildObject, Closure aSuccessClosure){
        aSuccessClosure(buildObject)
        return buildObject
    }

    /**
     * Execute aFailureClosure if the Validation was not ok. Errors organized in a SetMultimap<Map.Entry<String, Object>, String> will be provided as parameter to the fail
     * closure, for example:
     *
     * <pre>
     * Error list (SetMultimap<Map.Entry<String, Object>, String>):
     *
     * [class: MyClass] -> [name.mandatory.error, address.mandatory.error, age.min.error]
     * [property: name] -> [name.mandatory.error]
     * [property: address] -> [address.mandatory.error]
     * [property: name] -> [age.min.error]
     * </pre>
     *
     * @see com.google.common.collect.SetMultimap
     */

    static onBuildFailure(Object buildObject, Closure aFailureClosure){
        return buildObject
    }


}
