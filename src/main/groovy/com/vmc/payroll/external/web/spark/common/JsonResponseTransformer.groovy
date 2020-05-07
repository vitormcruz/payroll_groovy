package com.vmc.payroll.external.web.spark.common


import spark.ResponseTransformer

class JsonResponseTransformer implements ResponseTransformer {

    @Override
    String render(Object model) throws Exception {
        return model.toJson()
    }
}
