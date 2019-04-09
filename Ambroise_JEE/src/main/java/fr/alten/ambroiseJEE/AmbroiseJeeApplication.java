/**
 * 
 */ 
package fr.alten.ambroiseJEE;
 

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import fr.alten.ambroiseJEE.filter.TokenFilter;
import fr.alten.ambroiseJEE.utils.CustomLogger;
import fr.alten.ambroiseJEE.utils.LogLevel;



@SpringBootApplication
public class AmbroiseJeeApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmbroiseJeeApplication.class, args);
	}
	
	/**
	 * 
	 * @param mongoDbFactory
	 * @param context
	 * @return mongo template
	 * @author Andy Chabalier
	 */
	@Bean
	public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory, MongoMappingContext context) {

		MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory), context);
		converter.setTypeMapper(new DefaultMongoTypeMapper(null));

		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory, converter);

		return mongoTemplate;

	}
	
	/**
	 * Registers token filter to filter chain
	 * 
	 * @see TokenFilter
	 * @return registration bean
	 */
	@Bean
	public FilterRegistrationBean<TokenFilter> filterRegistrationBean() {
		FilterRegistrationBean<TokenFilter> registrationBean = new FilterRegistrationBean<TokenFilter>();
		TokenFilter tokenFilter = new TokenFilter();

		registrationBean.setFilter(tokenFilter);
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(1); // set precedence
		return registrationBean;
	}

}
