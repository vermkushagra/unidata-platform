package org.unidata.mdm.web;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.unidata.mdm.configuration.SpringConfiguration;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.EnumSet;

public class ApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        final AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(SpringConfiguration.class);

        servletContext.addListener(new ContextLoaderListener(applicationContext));

        final ServletRegistration.Dynamic dispatcher = servletContext.addServlet("cxfDispatcher", new CXFServlet());
        dispatcher.setLoadOnStartup(2);
        dispatcher.addMapping("/api/*");

        final FilterRegistration.Dynamic corsFilter =
                servletContext.addFilter("CorsFilter", "org.apache.catalina.filters.CorsFilter");
        corsFilter.setInitParameter("cors.allowed.origins", "*");
        corsFilter.setInitParameter("cors.allowed.methods", "GET,POST,HEAD,OPTIONS,PUT,DELETE");
        corsFilter.setInitParameter("cors.allowed.headers", "Content-Type,X-Requested-With,accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization,PROLONG_TTL");
        corsFilter.setInitParameter("cors.exposed.headers", "Access-Control-Allow-Origin,Access-Control-Allow-Credentials,Content-Disposition,Authorization,PROLONG_TTL");
        corsFilter.addMappingForServletNames(
                EnumSet.allOf(DispatcherType.class),
                true,
                "cxfDispatcher"
        );
    }
}
