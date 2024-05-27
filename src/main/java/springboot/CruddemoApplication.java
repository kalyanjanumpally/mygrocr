package springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import springboot.properties.TenantDbProperties;
import springboot.properties.FileStorageProperties;

@SpringBootApplication

@EnableConfigurationProperties({
	TenantDbProperties.class,
	FileStorageProperties.class
})
public class CruddemoApplication  extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(CruddemoApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(CruddemoApplication.class);
	}

}
