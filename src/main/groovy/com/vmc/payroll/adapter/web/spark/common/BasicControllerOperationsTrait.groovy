package com.vmc.payroll.adapter.web.spark.common

import com.vmc.payroll.adapter.web.spark.servlet.ValidationAwareResponse
import com.vmc.validationNotification.ApplicationValidationNotifier
import spark.Response
import spark.Route

trait BasicControllerOperationsTrait<IDTYPE> {

    Route r(Route route){
        return { req, res ->
            def validationAwareResponse = validationAwareResponse(res)
            def result = route(req, validationAwareResponse)
            validationAwareResponse.setBody(result)
            validationAwareResponse.fillResponse()
            return res.body()
        }
    }

    ValidationAwareResponse validationAwareResponse(Response res) {
        def validationAwareResponse = new ValidationAwareResponse(res)
        ApplicationValidationNotifier.addObserver(validationAwareResponse)
        return validationAwareResponse
    }

    Object getResource(IDTYPE id, resourceRepository) {
        def resource = resourceRepository.get(id)
        if (!resource) throw new ResourceNotFoundException()
        return resource
    }

}