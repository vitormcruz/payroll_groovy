package com.vmc.concurrency


import static com.vmc.concurrency.ObjectTracker.META_LANG_METHODS

trait ProxyTrait implements GroovyInterceptable {

    public subject

    def invokeMethod(String methodName, Object args) {
        if(META_LANG_METHODS.contains(methodName)){
            return this.getMetaMethod(methodName, args).invoke(this, args)
        }

        return this.@"com_vmc_concurrency_ProxyTrait__subject".invokeMethod(methodName, args)
    }

    def getProperty(String propertyName) {
        return this.@"com_vmc_concurrency_ProxyTrait__subject".getProperty(propertyName)
    }

    void setProperty(String propertyName, Object newValue){
        this.@"com_vmc_concurrency_ProxyTrait__subject".setProperty(propertyName, newValue)
    }
}