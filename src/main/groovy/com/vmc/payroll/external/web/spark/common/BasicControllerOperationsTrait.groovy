package com.vmc.payroll.external.web.spark.common


import com.vmc.payroll.external.web.spark.servlet.ValidationAwareResponse
import com.vmc.validationNotification.ApplicationValidationNotifier
import spark.Response
import spark.Route

trait BasicControllerOperationsTrait {

    Route r(Closure route){
        return { req, res ->
            def validationAwareResponse = validationAwareResponse(res)
            route(req, validationAwareResponse)
            validationAwareResponse.fillResponse()
            return res.body()
        }
    }

    ValidationAwareResponse validationAwareResponse(Response res) {
        def validationAwareResponse = new ValidationAwareResponse(res)
        ApplicationValidationNotifier.addObserver(validationAwareResponse)
        return validationAwareResponse
    }

    Object getResource(long employeeId, resourceRepository) {
        def resource = resourceRepository.get(employeeId)
        if (!resource) throw new ResourceNotFoundException()
        return resource
    }

}