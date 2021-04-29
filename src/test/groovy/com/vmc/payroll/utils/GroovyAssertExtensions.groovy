package com.vmc.payroll.utils

class GroovyAssertExtensions {

    public static final int TIMEOUT = 3000

    static void assertWaitingSuccess(Object testObject, assertion, timeout=TIMEOUT, originalError=null){
        try {
            sleep(100)
            timeout -= 100
            assertion()
        } catch (AssertionError e) {
            if(timeout < 0) throw originalError? originalError : e
            assertWaitingSuccess(testObject, assertion, timeout, originalError? originalError : e)
        }
    }
}
