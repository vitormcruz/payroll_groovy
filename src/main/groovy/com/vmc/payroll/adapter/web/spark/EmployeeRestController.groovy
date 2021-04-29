package com.vmc.payroll.adapter.web.spark

import com.cedarsoftware.util.io.JsonReader
import com.vmc.payroll.adapter.web.spark.common.BasicControllerOperationsTrait
import com.vmc.payroll.adapter.web.spark.common.SparkRestController
import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository
import com.vmc.payroll.domain.payment.delivery.AccountTransfer
import com.vmc.payroll.domain.payment.delivery.Mail
import com.vmc.payroll.domain.payment.delivery.Paymaster
import com.vmc.payroll.domain.payment.type.Commission
import com.vmc.payroll.domain.payment.type.Hourly
import com.vmc.payroll.domain.payment.type.Monthly

import static java.util.Optional.ofNullable
import static spark.Spark.*

class EmployeeRestController implements BasicControllerOperationsTrait<String>, SparkRestController{

    private Repository<Employee> employeeRepository
    private Map paymentTypeMap = ["monthly": this.&getMonthlyProvider,
                                  "commission": this.&getCommissionProvider,
                                  "hourly" : this.&getHourlyProvider
                                 ]

    private Map deliveryPayMap = ["accountTransfer": this.&getAccountTransferPayDelivery,
                                  "mailPayDelivery": this.&getMailPayDelivery,
                                  "paymasterPayDelivery" : this.&getPaymasterPayDelivery
                                 ]

    EmployeeRestController(Repository<Employee> anEmployeeRepository) {
        this.employeeRepository = anEmployeeRepository
    }

    @Override
    void configure() {

        path("/employee", {

            post("", r { req, res ->
                Map<String, String> data = JsonReader.jsonToJava(req.body(), [USE_MAPS : true])

                def paymentTypeBuilder = ofNullable(paymentTypeMap.get(data["paymentType"]))
                                            .orElse({ null })(data)
                def paymentDeliveryBuilder = ofNullable(deliveryPayMap.get(data["paymentDelivery"]))
                                                .orElse({ null })(data)

                def newEmployee = Employee.newEmployee(data["name"], data["address"], data["email"],
                                                       paymentTypeBuilder, paymentDeliveryBuilder)

                return newEmployee.onBuildSuccess { employeeRepository.add(newEmployee) }
            })

            patch("/:id", r {req, res  ->
                Employee employeeToChange = getResource(req.params(":id"), employeeRepository)
                Map<String, String> data = JsonReader.jsonToJava(req.body(), [USE_MAPS : true])

                def paymentTypeBuilder = ofNullable(paymentTypeMap.get(data["paymentType"]))
                        .orElse({ null })(data)
                def paymentDeliveryBuilder = ofNullable(deliveryPayMap.get(data["paymentDelivery"]))
                        .orElse({ null })(data)

                data["name"]?.with { employeeToChange.setName(it) }
                data["address"]?.with { employeeToChange.setAddress(it) }
                data["email"]?.with { employeeToChange.setEmail(it) }
                paymentTypeBuilder?.with { employeeToChange.bePaid(it) }
                paymentDeliveryBuilder?.with { employeeToChange.receivePaymentBy(it) }
                return employeeToChange
            })

            delete("/:id", r {req, res  ->
                Employee employeeToRemove = getResource(req.params(":id"), employeeRepository)
                employeeRepository.remove(employeeToRemove)
                return employeeToRemove
            })

            get("", r { req, res ->
                return new ArrayList(employeeRepository)
            })

        })
    }

    def getMonthlyProvider(Map<String, String> data){
        return { Monthly.newPaymentType(it, data["salary"] as Integer) }
    }

    def getCommissionProvider(Map<String, String> data){
        return { Commission.newPaymentType(it, data["salary"] as Integer, data["commissionRate"] as Integer) }
    }
    def getHourlyProvider(Map<String, String> data){
        return { Hourly.newPaymentType(it, data["hourRate"] as Integer) }
    }

    def getMailPayDelivery(Map<String, String> data) {
        return { Mail.newPaymentDelivery(it, data["street"])  }
    }

    def getPaymasterPayDelivery(Map<String, String> data) {
        return { Paymaster.newPaymentDelivery(it) }
    }

    def getAccountTransferPayDelivery(Map<String, String> data) {
        return { AccountTransfer.newPaymentDelivery(it, data["bank"], data["account"]) }
    }

}
