package com.vmc.smartfactory

//TODO shouldn't use sun, but I could'nt find better API for glob
import sun.nio.fs.Globs

/**
 */
class SmartFactory {
    private static SmartFactory dFactoryInstance = new SmartFactory()

    private Map<String, Configuration> configurations = new Hashtable<Class, Configuration>()

    def static SmartFactory instance(){
        return dFactoryInstance
    }

    def <T> T instanceForCallerOf(Class caller, Class<T> aClass) {
        def configuration = getConfigurationThatMatches(caller.getName())
        if(configuration == null){
            return null
        }

        return configuration.get(aClass)
    }

    def Configuration configurationFor(String glob) {
        def regexGlob = Globs.toUnixRegexPattern(glob)
        def configuration = configurations.get(regexGlob)
        if(configuration == null){
            configuration = new Configuration()
            configurations.put(regexGlob, configuration)
        }

        return configuration
    }

    def Configuration getConfigurationThatMatches(String context) {
        configurations.com
        return configurations.entrySet().find {
            return context ==~ it.key
        }?.value
    }
}
