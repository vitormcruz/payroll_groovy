package com.vmc.payroll.config


import com.vaadin.flow.server.VaadinServlet

import javax.servlet.annotation.WebServlet

@WebServlet(urlPatterns = "/*", name = "VaadinServlet")
class VaadinPayrollServletConfig extends VaadinServlet {

}
