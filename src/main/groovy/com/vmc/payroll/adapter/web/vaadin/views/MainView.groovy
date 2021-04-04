package com.vmc.payroll.adapter.web.vaadin.views

import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.material.Material
import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository

@Route("")
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-app-layout-styles.css", themeFor = "vaadin-app-layout")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@Theme(value = Material)
class MainView extends AppLayout {

    Repository<Employee> employees


    MainView() {
        notifyInstanceCreated(this)
        Tabs tabs = new Tabs(new Tab("List Employees"), new Tab("Process Payroll"))
        tabs.setOrientation(Tabs.Orientation.VERTICAL)
        addToDrawer(tabs)
        addToNavbar(new DrawerToggle())
        def grid = new Grid<Employee>(Employee)
        grid.setItems(createEmployees())
        grid.setColumns("name", "address", "email")
        setContent(grid)
    }

    private List createEmployees() {
        return new ArrayList(employees)
    }

}
