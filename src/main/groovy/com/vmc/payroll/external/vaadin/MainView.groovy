package com.vmc.payroll.external.vaadin

import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.material.Material

@Route("")
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@Theme(value = Material)
class MainView extends VerticalLayout {

    MainView() {
        // Use TextField for standard text input
        TextField textField = new TextField("Your name")
        textField.addThemeName("bordered")

        // Button click listeners can be defined as lambda expressions
        Button button = new Button("Say hello", {
                e -> Notification.show("Oie!")
        })

        // Theme variants give you predefined extra styles for components.
        // Example: Primary button is more prominent look.
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY)

        // You can specify keyboard shortcuts for buttons.
        // Example: Pressing enter in this view clicks the Button.
        button.addClickShortcut(Key.ENTER)

        // Use custom CSS classes to apply styling. This is defined in shared-styles.css.
        addClassName("centered-content")

        add(textField, button)
    }

}
