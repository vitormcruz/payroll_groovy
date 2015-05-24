package sandbox
import com.vaadin.server.VaadinServlet
import org.atmosphere.cpr.SessionSupport
import org.hibernate.SessionFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.embedded.ServletRegistrationBean
import org.springframework.boot.context.web.SpringBootServletInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.listener.SimpleMessageListenerContainer
import org.springframework.jms.listener.adapter.MessageListenerAdapter
import sandbox.heavyValidation.AsyncHeavyValidation
import sandbox.heavyValidation.JMSAsyncHeavyValidation

import javax.jms.ConnectionFactory
import javax.servlet.ServletContext
import javax.servlet.ServletException
import javax.servlet.http.HttpSessionEvent
import javax.servlet.http.HttpSessionListener

@Configuration
@EnableAutoConfiguration
@ComponentScan
class SandboxApplication extends SpringBootServletInitializer{

    def static SessionFactory sessionFactory
    def static receiver = new MessageReceiver()
    def static AsyncHeavyValidation asyncHeavyValidation
    def static JmsTemplate jmsTemplate
    public static final String MAIL_BOX = "sandbox-message-box"

    /**
     * The first method, configure, is used to define this class as the configuration class of spring in a normal
     * web server. The second is used to achieve the same goal and also to make possible for spring to start an embedded
     * web server. It would be better if the main method wasn't necessary.
     */

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.sources(SandboxApplication.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SandboxApplication, args);
    }

    @Override
    void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext)
        servletContext.setInitParameter("productionMode", "false")
        servletContext.addListener([sessionCreated : {HttpSessionEvent se -> se.getSession().setMaxInactiveInterval(60*60*24)} ,
                                    sessionDestroyed : {}] as HttpSessionListener)
        sessionFactory = buildSessionFactory()
    }

    @Bean
    public ServletRegistrationBean vaadinServlet(){
        ServletRegistrationBean registration = new ServletRegistrationBean(new VaadinServlet(), "/sandbox/*", "/VAADIN/*");

        Map<String, String> params = new HashMap<String, String>();
        params.put("UI", "sandbox.SandboxUI");
        params.put("async-supported", "true")
        params.put("org.atmosphere.useWebSocketAndServlet3", "true")

        registration.setInitParameters(params);
        return registration;
    }

    public SessionFactory buildSessionFactory(){
        return new org.hibernate.cfg.Configuration().addResource("sandbox/payroll/persistence/hibernate/mapping/Employee.hbm.xml")
                                             .setProperty("dialect", "org.hibernate.dialect.HSQLDialect")
                                             .setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver")
                                             .setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:tsg")
                                             .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                                             .setProperty("hibernate.connection.username", "sa")
                                             .setProperty("hibernate.connection.password", "")
                                             .setProperty("hibernate.show_sql", "true")
                                             .buildSessionFactory()



    }

    static SessionFactory getSessionFactory() {
        return sessionFactory
    }

    @Bean
    public SessionSupport sessionSupport(){
        return new SessionSupport()
    }

    @Bean
    MessageListenerAdapter adapter() {
        MessageListenerAdapter messageListener = new MessageListenerAdapter(receiver);
        messageListener.setDefaultListenerMethod("receiveMessage");
        return messageListener;
    }

    @Bean
    SimpleMessageListenerContainer container(MessageListenerAdapter messageListener,
                                             ConnectionFactory connectionFactory,
                                             ConfigurableApplicationContext context) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setMessageListener(messageListener);
        container.setConnectionFactory(connectionFactory);
        container.setDestinationName(MAIL_BOX);
        jmsTemplate = context.getBean(JmsTemplate.class)
        asyncHeavyValidation = new JMSAsyncHeavyValidation()
        return container;
    }


}