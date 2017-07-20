package com.vmc.payroll.external.presentation.vaadin.view.payment

import com.vaadin.ui.TextField
import com.vmc.payroll.external.presentation.vaadin.view.components.DynamicComboBox
import com.vmc.payroll.external.presentation.vaadin.view.employee.VaadinEmployeeDTO
import com.vmc.payroll.payment.delivery.AccountTransfer
import com.vmc.payroll.payment.delivery.Mail
import com.vmc.payroll.payment.delivery.Paymaster
import com.vmc.payroll.payment.delivery.api.PaymentDelivery
import com.vmc.validationNotification.vaadin.BinderDecoratorForValidationNotification
import org.reflections.Reflections

class PaymentDeliveryClassViewExtensions {

    private static Set<Class> paymentDeliveryClasses = new Reflections("com.vmc.payroll.payment.delivery").getSubTypesOf(PaymentDelivery)

    static ArrayList myVaadinComponents(PaymentDelivery paymentDelivery, BinderDecoratorForValidationNotification binder){
        def paymentDeliveryComboBox = new DynamicComboBox("Select a payment delivery: ",
                                                            paymentDeliveryClasses,
                                                            PaymentDelivery.getDefaultPaymentDelivery(),
                                                            binder, { it.getSimpleName() })

        binder.bind(paymentDeliveryComboBox.comboBox, "paymentDelivery")
        return paymentDeliveryComboBox.components

    }

    static getDefaultPaymentDelivery(PaymentDelivery paymentDelivery){
        return Paymaster
    }

    static myVaadinComponents(Paymaster paymaster, BinderDecoratorForValidationNotification binder){
        return []
    }

    static myVaadinComponents(Mail mail, BinderDecoratorForValidationNotification binder){
        return [new TextField("Payment Address: ").with {
                        it.setRequiredIndicatorVisible(true)
                        binder.bind(it, "mailAddress")
                        binder.bindValidationFor(it, Mail, "address")
                        it
                        }
               ]
    }

    static myVaadinComponents(AccountTransfer accountTransfer, BinderDecoratorForValidationNotification binder){
        return [new TextField("Bank: ").with {
                                it.setRequiredIndicatorVisible(true)
                                binder.bind(it, "bank")
                                binder.bindValidationFor(it, AccountTransfer, "bank")
                                it
                              },
                new TextField("Account: ").with {
                                    it.setRequiredIndicatorVisible(true)
                                    binder.bind(it, "account")
                                    binder.bindValidationFor(it, AccountTransfer, "account")
                                    it
                             }]
    }

    static fromVaadinDTO(Paymaster paymaster, VaadinEmployeeDTO dto){
        return {Paymaster.newPaymentDelivery(it)}
    }

    static fromVaadinDTO(Mail mail, VaadinEmployeeDTO dto){
        return {Mail.newPaymentDelivery(it, dto.mailAddress)}
    }

    static fromVaadinDTO(AccountTransfer accountTransfer, VaadinEmployeeDTO dto){
        return {AccountTransfer.newPaymentDelivery(it, dto.bank, dto.account)}
    }

}
