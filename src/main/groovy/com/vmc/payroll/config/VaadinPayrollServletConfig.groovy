package com.vmc.payroll.config

import com.vaadin.flow.server.VaadinServlet

import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet

@WebServlet(urlPatterns = "/*", name = "VaadinServlet")
class VaadinPayrollServletConfig extends VaadinServlet {

    @Override
    void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig)

    }
}
