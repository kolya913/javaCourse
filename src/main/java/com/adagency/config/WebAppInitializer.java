package com.adagency.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class WebAppInitializer implements WebApplicationInitializer  {
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(AppConfig.class);
		
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("ADAgency2", new DispatcherServlet(rootContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");
		
		FilterRegistration.Dynamic encodingFilter = servletContext.addFilter("encodingFilter", new CharacterEncodingFilter());
		encodingFilter.setInitParameter("encoding", "UTF-8");
		encodingFilter.setInitParameter("forceEncoding", "true");
		encodingFilter.addMappingForUrlPatterns(null, false, "/*");
		
		FilterRegistration.Dynamic securityFilter = servletContext.addFilter("springSecurityFilterChain", new DelegatingFilterProxy());
		securityFilter.addMappingForUrlPatterns(null, false, "/*");
		
		servletContext.setSessionTimeout(15);
	}
	
}
