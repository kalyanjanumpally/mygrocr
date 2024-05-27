package springboot.admin.homeCompany.tenants;

import java.time.LocalTime;

import lombok.Data;

@Data
public class DTOTenantDetails {
	
	String tenantUrl;
	LocalTime shopOpenTime;
	LocalTime shopCloseTime;
	Boolean tenantOpen;

}
