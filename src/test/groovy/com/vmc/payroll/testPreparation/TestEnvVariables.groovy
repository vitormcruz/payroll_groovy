package com.vmc.payroll.testPreparation

class TestEnvVariables {

    //Describes how much the machine has degraded performance compared to the one conceived for the test
    //This absolutely makes no sense in a real application, in which a mirror machine should be used in a
    // performance-like test, but as this is a toy project, I must use free cloud machines which are
    // usually very limited :)
    public static final Integer testMachinePerformanceDegradationFactor

    static{
        def envValue = System.getenv("TEST_MACHINE_PERFORMANCE_DEGRADATION_FACTOR")?: "0"
        testMachinePerformanceDegradationFactor = envValue.isInteger() ? envValue as Integer : 0
    }
}
