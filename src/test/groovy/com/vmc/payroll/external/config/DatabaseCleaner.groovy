package com.vmc.payroll.external.config

import com.vmc.concurrency.api.UserModelSnapshot
import com.vmc.payroll.domain.api.Repository

class DatabaseCleaner {
    private UserModelSnapshot model
    private Repository[] repositories

    DatabaseCleaner(UserModelSnapshot aModel, Repository ...repositories) {
        this.repositories = repositories
        this.model = aModel
    }

    void cleanDatabase(){
        repositories.each {it.clear()}
        model.save()
    }


}
