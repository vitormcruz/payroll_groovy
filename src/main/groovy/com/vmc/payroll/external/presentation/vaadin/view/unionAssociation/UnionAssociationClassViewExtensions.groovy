package com.vmc.payroll.external.presentation.vaadin.view.unionAssociation

import com.vaadin.data.HasValue
import com.vaadin.ui.RadioButtonGroup
import com.vaadin.ui.TextField
import com.vmc.payroll.domain.payment.type.api.PaymentType
import com.vmc.payroll.domain.unionAssociation.api.UnionAssociation
import com.vmc.validationNotification.vaadin.BinderDecoratorForValidationNotification
import org.reflections.Reflections

class UnionAssociationClassViewExtensions {

    private static Set<Class> unionAssociationClasses = new Reflections("com.vmc.payroll.domain.payment.type").getSubTypesOf(PaymentType)

    static ArrayList myVaadinComponents(UnionAssociation unionAssociation, BinderDecoratorForValidationNotification binder){
        def components = new ArrayList()
        def unionMembershipSection = createUnionMembershipSection()
        components.add(createIsUnionMemberOption(unionMembershipSection))
        components.add(unionMembershipSection)
        return components
    }

    static RadioButtonGroup<String> createIsUnionMemberOption(TextField unionMembershipSection) {
        return new RadioButtonGroup<String>("Is a Union Member").with {
            it.setItems(["Yes", "No"])
            it.setValue("No")
            it.addValueChangeListener({ event -> isUnionMember(event, unionMembershipSection) })
            it
        }
    }

    static TextField createUnionMembershipSection() {
        return new TextField("Union Membership Rate: ").with { it.setRequiredIndicatorVisible(true)
            it.setVisible(false)
            it
        }
    }

    static def isUnionMember(HasValue.ValueChangeEvent<String> event, TextField unionMembershipSection) {
        if(event.getValue() == "Yes" && event.getOldValue() == "No"){
            unionMembershipSection.setVisible(true)
        }else if(event.getValue() == "No" && event.getOldValue() == "Yes"){
            unionMembershipSection.setVisible(false)
        }
    }
}
