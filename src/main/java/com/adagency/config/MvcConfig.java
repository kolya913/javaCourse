package com.adagency.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.adagency.Controllers")
@ComponentScan("com.adagency.config")
public class MvcConfig implements WebMvcConfigurer {
	
	public static final String RESOURCE_PATH = System.getProperty("catalina.home") + "/webapps/ADAgency2/resources/";
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		/*String appRootPath = System.getProperty("catalina.home");
		String resourcePath = appRootPath + "/webapps/ADAgency2/resources/";*/
		
		registry
				.addResourceHandler("/resources/**")
				.addResourceLocations("file:" + RESOURCE_PATH);
	}
	
	@Bean
	public ViewResolver viewResolver() {
		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
		resolver.setTemplateEngine(templateEngine());
		resolver.setCharacterEncoding("UTF-8");
		return resolver;
	}
	
	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setTemplateResolver(templateResolver());
		engine.addDialect(new SpringSecurityDialect());
		return engine;
	}
	
	@Bean
	public SpringResourceTemplateResolver templateResolver() {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setSuffix(".html");
		resolver.setTemplateMode("HTML");
		resolver.setCharacterEncoding("UTF-8");
		return resolver;
	}
	
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		StringHttpMessageConverter converter = new StringHttpMessageConverter();
		converter.setDefaultCharset(StandardCharsets.UTF_8);
		converters.add(converter);
	}
	
	@Bean
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setMaxUploadSize(-1); // -1 indicates no limit
		return resolver;
	}
	


}

