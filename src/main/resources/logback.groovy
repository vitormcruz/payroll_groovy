import ch.qos.logback.classic.encoder.PatternLayoutEncoder

appender("stdout", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%d %p [%c] - %m%n"
  }
}

root(WARN, ["stdout"])

logger("spark.**", DEBUG)
logger("org.reflections.**", ERROR)
