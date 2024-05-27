package springboot.properties;


	
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "tenantdb")
@Getter
@Setter
public class TenantDbProperties {
	private String tenantsDbUrl;
	private String tenantDbUsername;
	private String tenantDbPassword;

	
	
	
	
}




