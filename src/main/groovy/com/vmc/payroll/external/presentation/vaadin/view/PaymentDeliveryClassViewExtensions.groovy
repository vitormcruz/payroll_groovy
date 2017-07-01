package com.vmc.payroll.external.presentation.vaadin.view

import com.vaadin.ui.TextField
import com.vmc.payroll.payment.delivery.AccountTransfer
import com.vmc.payroll.payment.delivery.Mail
import com.vmc.payroll.payment.delivery.Paymaster

class PaymentDeliveryClassViewExtensions {

    static myVaadinComponents(Paymaster paymaster){
        return []
    }

    static myVaadinComponents(Mail mail){
        return [new TextField("Payment Address: ").with {it.setRequiredIndicatorVisible(true); it}]
    }

    static myVaadinComponents(AccountTransfer accountTransfer){
        return [new TextField("Bank: ").with { it.setRequiredIndicatorVisible(true); it },
                new TextField("Account: ").with { it.setRequiredIndicatorVisible(true); it }]
    }

}
