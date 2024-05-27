package springboot.admin.homeCompany.tenants;

import lombok.Data;

@Data
public class DTOEmployeeTenantPasswordReset {
	
	private int tenantId;
	
	private String tenantUrl;
	
	private String newPassword;

}
