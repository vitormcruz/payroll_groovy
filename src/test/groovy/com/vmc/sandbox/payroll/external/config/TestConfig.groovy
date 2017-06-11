package com.vmc.sandbox.payroll.external.config

import com.vmc.payroll.external.config.Config
import org.detangle.smartfactory.SmartFactory


class TestConfig implements Config{

    private smartFactory = SmartFactory.instance()

    @Override
    void configure() {
        def globalConfiguration = smartFactory.configurationFor("com.vmc.sandbox.payroll.**")
        globalConfiguration.put(DatabaseCleaner, new DatabaseCleaner())
    }

    @Override
    void tearDown() {

    }
}
