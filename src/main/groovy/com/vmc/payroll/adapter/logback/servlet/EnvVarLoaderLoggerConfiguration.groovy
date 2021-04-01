package com.vmc.payroll.adapter.logback.servlet

import ch.qos.logback.classic.Level
import org.slf4j.LoggerFactory

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener

class EnvVarLoaderLoggerConfiguration implements ServletContextListener {

    @Override
    void contextInitialized(ServletContextEvent sce) {
        System.getenv()
              .findAll {it.key.startsWith("logger.")}
              .each {
                  setLoger(it.key.replaceFirst("logger.", ""), Level.toLevel(it.value))
              }
    }

    void setLoger(String loggerName, Level level) {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(loggerName)).setLevel(level)
    }

    @Override
    void contextDestroyed(ServletContextEvent sce) {
        //do nothing
    }
}
