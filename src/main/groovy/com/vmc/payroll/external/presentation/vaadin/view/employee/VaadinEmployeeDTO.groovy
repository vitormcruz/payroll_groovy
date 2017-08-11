package com.vmc.payroll.external.presentation.vaadin.view.employee

import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.payment.delivery.api.PaymentDelivery
import com.vmc.payroll.domain.payment.type.api.PaymentType
import com.vmc.payroll.domain.unionAssociation.api.UnionAssociation

class VaadinEmployeeDTO {

    String name
    String address
    String email

    Class<PaymentType> paymentType
    Integer hourRate
    Integer salary
    Integer commissionRate

    Class<PaymentDelivery> paymentDelivery
    String bank
    String account
    String mailAddress

    Class<UnionAssociation> unionAssociation
    Integer rate

    Employee toEntity() {
        return Employee.newEmployee(name, address, email, paymentType.fromVaadinDTO(this), paymentDelivery.fromVaadinDTO(this))
    }
}
