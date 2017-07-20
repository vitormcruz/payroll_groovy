package com.vmc.validationNotification

class ObjectValidationNotificationExtensions {

    static String issueError(Object subject, String message){
        ApplicationValidationNotifier.issueError(subject, [:], message)
    }

    static String issueError(Object subject, String instantValidationName, String message){
        ApplicationValidationNotifier.issueError(subject, [:], instantValidationName, message)
    }

    static String issueError(Object subject, String message, Map mapEntries){
        ApplicationValidationNotifier.issueError(subject, mapEntries, message)
    }

    static String issueError(Object subject, String instantValidationName, String message, Map mapEntries){
        ApplicationValidationNotifier.issueError(subject, mapEntries, instantValidationName, message)
    }

    /**
     * Execute aSuccessClosure if the object building was ok passing myself as parameter.
     */
    static onBuildSucess(Object buildObject, Closure aSuccessClosure){
        return aSuccessClosure(this)
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
        return null
    }


}
