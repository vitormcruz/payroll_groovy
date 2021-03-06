package com.vmc.validationNotification.servlet

import com.vmc.validationNotification.ApplicationValidationNotifier

import javax.servlet.*

class ValidationNotifierFilter implements Filter{

    @Override
    void init(FilterConfig filterConfig) throws ServletException {
        //Nothing to do
    }

    @Override
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ApplicationValidationNotifier.createCurrentListOfListeners()

        try {
            chain.doFilter(request,response)
        } finally {
            ApplicationValidationNotifier.destroyCurrentListOfListeners()
        }
    }

    @Override
    void destroy() {
        //Nothing to do
    }
}
