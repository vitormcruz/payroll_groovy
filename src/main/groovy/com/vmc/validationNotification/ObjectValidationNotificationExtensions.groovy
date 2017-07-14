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
}
