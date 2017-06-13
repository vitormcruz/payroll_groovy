package com.vmc.payroll.external.config

import com.vmc.concurrency.ModelSnapshot
import com.vmc.payroll.api.Repository

class DatabaseCleaner {
    private ModelSnapshot model
    private Repository[] repositories

    DatabaseCleaner(ModelSnapshot aModel, Repository ...repositories) {
        this.repositories = repositories
        this.model = aModel
    }

    void cleanDatabase(){
        repositories.each {it.clear()}
        model.save()
    }


}