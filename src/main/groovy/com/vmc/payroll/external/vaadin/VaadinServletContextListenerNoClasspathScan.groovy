package com.vmc.payroll.external.vaadin

import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.dependency.JavaScript
import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.component.dependency.NpmPackage
import com.vaadin.flow.router.InternalServerError
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouteNotFoundError
import com.vaadin.flow.server.startup.DevModeInitializer
import com.vaadin.flow.server.startup.ErrorNavigationTargetInitializer
import com.vaadin.flow.server.startup.RouteRegistryInitializer
import com.vaadin.flow.server.startup.ServletContextListeners
import com.vaadin.flow.theme.Theme
import org.reflections.Reflections

import javax.servlet.ServletContainerInitializer
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.ServletException

class VaadinServletContextListenerNoClasspathScan implements ServletContextListener {

    private ServletContextListeners servletContextListeners = new ServletContextListeners()

    @Override
    void contextInitialized(ServletContextEvent sce) {
        // Run initializers with relevant classes from the classpath
        runInitializers(sce)

        // Finalize initialization
        servletContextListeners.contextInitialized(sce)
    }

    private static void runInitializers(ServletContextEvent sce) {
        // Anything implementing HasErrorParameter
        runInitializer(new ErrorNavigationTargetInitializer(), sce, RouteNotFoundError.class,
                       InternalServerError.class)

        // @Route
        Reflections reflectionsVaadinViews = new Reflections("com.vmc.payroll.external.vaadin.views")
        runInitializer(new RouteRegistryInitializer(), sce, reflectionsVaadinViews.getTypesAnnotatedWith(Route) as Class[])

        def packagesToScan = ["com.vaadin", "com.vmc.payroll.external.vaadin"]
        def classesWithInterestedAnnotations =
                packagesToScan.collect { packageToScan ->
                    Reflections reflectionsVaadinComponents = new Reflections(packageToScan)
                    return reflectionsVaadinComponents.getTypesAnnotatedWith(NpmPackage) +
                           reflectionsVaadinComponents.getTypesAnnotatedWith(JsModule) +
                           reflectionsVaadinComponents.getTypesAnnotatedWith(CssImport) +
                           reflectionsVaadinComponents.getTypesAnnotatedWith(JavaScript) +
                           reflectionsVaadinComponents.getTypesAnnotatedWith(Theme)
        }.flatten()

        // @NpmPackage, @JsModule, @CssImport, @JavaScript or @Theme
        runInitializer(new DevModeInitializer(), sce, classesWithInterestedAnnotations as Class[])
    }

    private static void runInitializer(ServletContainerInitializer initializer, ServletContextEvent sce,
                                       Class<?>... types) {
        try {
            initializer.onStartup(new HashSet<>(Arrays.asList(types)), sce.getServletContext())
        } catch (ServletException e) {
            throw new RuntimeException(e)
        }
    }

    @Override
    void contextDestroyed(ServletContextEvent sce) {
        servletContextListeners.contextDestroyed(sce)
    }
}
