package com.vmc.payroll.external.presentation.vaadin

import com.vaadin.annotations.Theme
import com.vaadin.annotations.Title
import com.vaadin.server.VaadinRequest
import com.vaadin.ui.AbsoluteLayout
import com.vaadin.ui.UI

@Title("Payroll")
@Theme("valo")
class PayrollUI extends UI {

    protected void init(VaadinRequest request) {
        def mainContent = new AbsoluteLayout()
        setContent(mainContent)
        mainContent.addComponent(new EmployeeListView())
    }
}
