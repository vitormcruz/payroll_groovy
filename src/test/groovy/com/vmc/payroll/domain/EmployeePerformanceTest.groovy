package com.vmc.payroll.domain


import com.vmc.instantiation.extensions.ObjectMother
import com.vmc.payroll.domain.api.Repository
import com.vmc.payroll.testPreparation.IntegrationTestBase
import com.vmc.payroll.testPreparation.TestEnvVariables
import com.vmc.userModel.GeneralUserModel
import com.vmc.userModel.api.UserModel
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class EmployeePerformanceTest extends IntegrationTestBase {

    private static Repository<Employee> employeeRepository

    private static ObjectMother<Employee> employeeMother
    public static final int MAX_TIME_EXECUTION = 9000 * (TestEnvVariables.testMachinePerformanceDegradationFactor + 1)

    def benchmark = { closure ->
      def start = System.currentTimeMillis()
      closure.call()
      def now = System.currentTimeMillis()
      now - start
    }

    @BeforeAll
    def static void setupAll(){
        def userModelSnapshot = new GeneralUserModel()
        UserModel.load(userModelSnapshot)
        employeeRepository = serviceLocator.employeeRepository
        employeeMother = EmployeeMother.randomEmployeeMother.configurePostBirthScript { newEmployee -> employeeRepository.add(newEmployee)}
    }

    @Test
    void "Insert lots of different employees"(){
        assert benchmark {1000.times {employeeMother.createNewBorn()}} < MAX_TIME_EXECUTION
    }
}
