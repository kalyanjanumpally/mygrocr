package springboot.admin.homeCompany.tenants;

import java.util.List;

import lombok.Data;

@Data
public class ResponseTenantHome {
	
	private List<EntityTenant> tenants;
	
	private Long countOfTenants;

}
