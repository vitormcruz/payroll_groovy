package com.vmc.payroll.external.presentation.vaadin.view.employee

import com.vmc.payroll.Employee
import com.vmc.payroll.payment.delivery.api.PaymentDelivery
import com.vmc.payroll.payment.type.api.PaymentType
import com.vmc.payroll.unionAssociation.api.UnionAssociation
import com.vmc.validationNotification.builder.GenericBuilder

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

    GenericBuilder builderForEntity() {
        return new GenericBuilder(Employee).withName(name).withAddress(address).withEmail(email).withPaymentArgs(paymentType.fromVaadinDTO(this)).withPaymentDeliveryArgs(paymentDelivery.fromVaadinDTO(this))
    }
}
