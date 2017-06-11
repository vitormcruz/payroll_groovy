import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import org.apache.commons.io.IOUtils

import static ch.qos.logback.classic.Level.WARN

def appProperties = loadProperties()
def logpath = appProperties.get("logpath")

appender("stdout", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%d %p [%c] - %m%n"
  }
}

root(WARN, ["stdout"])
logger("org.flywaydb", DEBUG)
logger("com.querydsl.sql", DEBUG)


static def loadProperties(){
    InputStream appPropInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties")
    try {
        def properties = new Properties()
        properties.load((InputStream)appPropInputStream)
        appPropInputStream.close()
        return properties
    }finally {
        IOUtils.closeQuietly(appPropInputStream)
    }
}