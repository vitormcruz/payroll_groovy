package com.vmc.payroll.external.config

import com.vmc.concurrency.api.UserModel
import com.vmc.payroll.domain.api.Repository

class DatabaseCleaner {
    private UserModel model
    private Repository[] repositories

    DatabaseCleaner(UserModel aModel, Repository ...repositories) {
        this.repositories = repositories
        this.model = aModel
    }

    void cleanDatabase(){
        repositories.each {it.clear()}
        model.save()
    }


}
