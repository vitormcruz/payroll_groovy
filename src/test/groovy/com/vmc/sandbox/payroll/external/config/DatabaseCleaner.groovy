package com.vmc.sandbox.payroll.external.config

import com.vmc.concurrency.ModelSnapshot
import com.vmc.payroll.Repository

class DatabaseCleaner {
    private ModelSnapshot model
    private Repository[] repositories

    DatabaseCleaner(ModelSnapshot aModel, Repository ...repositories) {
        this.repositories = repositories
        this.model = aModel
    }

    public void cleanDatabase(){
        repositories.each {it.clear()}
        model.save()
    }


}
