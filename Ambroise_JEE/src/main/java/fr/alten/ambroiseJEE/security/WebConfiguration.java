package fr.alten.ambroiseJEE.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

	@Autowired
	private ApplicationContext ctx;

	@Override
	public void addCorsMappings(final CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("http://localhost:4200", ctx.getEnvironment().getProperty("security.allowOrigin"))
				.allowedMethods("GET", "PUT", "POST", "DELETE", "OPTIONS");
	}
}
