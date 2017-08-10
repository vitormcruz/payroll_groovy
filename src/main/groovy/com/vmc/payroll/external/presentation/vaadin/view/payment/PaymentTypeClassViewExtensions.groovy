package com.vmc.payroll.external.presentation.vaadin.view.payment

import com.vmc.payroll.external.presentation.vaadin.view.components.DynamicComboBox
import com.vmc.payroll.external.presentation.vaadin.view.employee.VaadinEmployeeDTO
import com.vmc.payroll.payment.type.Commission
import com.vmc.payroll.payment.type.Hourly
import com.vmc.payroll.payment.type.Monthly
import com.vmc.payroll.payment.type.GenericPaymentType
import com.vmc.payroll.payment.type.api.PaymentType
import com.vmc.validationNotification.vaadin.BinderDecoratorForValidationNotification
import org.reflections.Reflections
import org.vaadin.viritin.fields.IntegerField

class PaymentTypeClassViewExtensions {

    private static Set<Class> paymentTypeClasses

    static{
        paymentTypeClasses = new Reflections("com.vmc.payroll.payment.type").getSubTypesOf(PaymentType)
        paymentTypeClasses.remove(GenericPaymentType)
    }

    static ArrayList myVaadinComponents(PaymentType paymentType, BinderDecoratorForValidationNotification binder){
        def paymentTypeComboBox = new DynamicComboBox("Select a payment type: ",
                                                       paymentTypeClasses,
                                                       PaymentType.getDefaultPaymentType(),
                                                       binder, { it.getSimpleName() })

        binder.bind(paymentTypeComboBox.comboBox, "paymentType")
        return paymentTypeComboBox.components

    }

    static Collection<Class<PaymentType>> getAllPaymentTypes(PaymentType paymentType) {
        return paymentTypeClasses
    }

    static getDefaultPaymentType(PaymentType paymentType){
        return Monthly
    }

    static myVaadinComponents(Monthly monthly, BinderDecoratorForValidationNotification binder){
        return [new IntegerField("Salary: ").with {
                    it.setRequiredIndicatorVisible(true)
                    binder.bind(it, "salary")
                    binder.disableBindingFor(it)
                    binder.bindValidationFor(it, Monthly, "salary")
                    it
               }]
    }

    static myVaadinComponents(Hourly hourly, BinderDecoratorForValidationNotification binder){
        return [new IntegerField("Hour Rate: ").with {
                    it.setRequiredIndicatorVisible(true)
                    binder.bind(it, "hourRate")
                    binder.bindValidationFor(it, Hourly, "hourRate")
                    it
               }]
    }

    static myVaadinComponents(Commission commission, BinderDecoratorForValidationNotification binder){
        def commissionFields = Monthly.myVaadinComponents(binder)
        commissionFields.add(new IntegerField("Commission Rate: ").with {
                                it.setRequiredIndicatorVisible(true)
                                binder.forField(it)
                                binder.bind(it, "commissionRate")
                                binder.bindValidationFor(it, Commission, "commissionRate")
                                it
                            })
        return commissionFields
    }

    static fromVaadinDTO(Monthly monthly, VaadinEmployeeDTO dto){
        return {Monthly.newPaymentType(it, dto.salary)}
    }

    static fromVaadinDTO(Hourly hourly, VaadinEmployeeDTO dto){
        return {Hourly.newPaymentType(it, dto.hourRate)}
    }

    static fromVaadinDTO(Commission commission, VaadinEmployeeDTO dto){
        return {Commission.newPaymentType(it, dto.salary, dto.commissionRate)}
    }
}
