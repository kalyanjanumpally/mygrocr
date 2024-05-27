package springboot.admin.homeCompany.tenants;

import java.util.List;

import lombok.Data;

@Data
public class ResponseTenantsHome {	
	
	List<EntityTenant> tenants;
	
	Long countOfTenants;

}
