package com.vmc.payroll.external.presentation.vaadin.view

import com.vaadin.ui.TextField
import com.vmc.payroll.payment.type.Commission
import com.vmc.payroll.payment.type.Hourly
import com.vmc.payroll.payment.type.Monthly


class PaymentTypeClassViewExtensions {

    static myVaadinComponents(Monthly monthlyClass){
        return [new TextField("Salary: ").with {it.setRequiredIndicatorVisible(true); it}]
    }

    static myVaadinComponents(Hourly hourlyClass){
        return [new TextField("Hour Rate: ").with {it.setRequiredIndicatorVisible(true); it}]
    }

    static myVaadinComponents(Commission commissionClass){
        def commissionFields = Monthly.myVaadinComponents()
        commissionFields.add(new TextField("Hour Rate: ").with { it.setRequiredIndicatorVisible(true); it })
        return commissionFields
    }

}
