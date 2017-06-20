package com.vmc.payroll.external.presentation.vaadin

import com.vaadin.annotations.Theme
import com.vaadin.annotations.Title
import com.vaadin.server.VaadinRequest
import com.vaadin.ui.UI
import com.vaadin.ui.VerticalLayout

@Title("Payroll")
@Theme("valo")
class PayrollUI extends UI {

    protected void init(VaadinRequest request) {
        def mainContent = new VerticalLayout()
        mainContent.setMargin(false)
        setContent(mainContent)
        mainContent.addComponent(new EmployeeListView())
    }
}
