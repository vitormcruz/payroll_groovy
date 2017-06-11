package com.vmc.payroll.external.presentation.webservice.spark

import com.vmc.validationNotification.ApplicationValidationNotifier

trait BasicControllerOperationsTrait {

    public SparkControllerValidationListener getValidationListener() {
        def listener = new SparkControllerValidationListener()
        ApplicationValidationNotifier.addObserver(listener)
        return listener
    }

    public Object getResource(long employeeId, resourceRepository) {
        def resource = resourceRepository.get(employeeId)
        if (!resource) throw new ResourceNotFoundException()
        return resource
    }

}