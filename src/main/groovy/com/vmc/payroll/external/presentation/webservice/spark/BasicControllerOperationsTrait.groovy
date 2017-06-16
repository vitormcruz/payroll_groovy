package com.vmc.payroll.external.presentation.webservice.spark

import com.vmc.validationNotification.imp.ApplicationValidationNotifier

trait BasicControllerOperationsTrait {

    SparkControllerValidationListener getValidationListener() {
        def listener = new SparkControllerValidationListener()
        ApplicationValidationNotifier.addObserver(listener)
        return listener
    }

    Object getResource(long employeeId, resourceRepository) {
        def resource = resourceRepository.get(employeeId)
        if (!resource) throw new ResourceNotFoundException()
        return resource
    }

}