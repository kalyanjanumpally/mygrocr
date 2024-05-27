package springboot.adminTenant.tenant;

import java.sql.Time;
import java.time.LocalTime;

import lombok.Data;

@Data
public class DTOTenantDetails {
	
    Integer tenantId;
	String tenantName; 
	String tenantUrl; 
//	Boolean displayOutOfStockProductsBool;
	String tenantImageUrl; 
	String tenantContactPerson; 
	String tenantPhoneNumber1;
	String tenantPhoneNumber2; 
	String tenantAddress; 
	String tenantArea;
	String tenantCity; 
	String tenantPostalCode;
	String tenantEmail; 
	Boolean tenantActive; 
	Boolean tenantDeleteStatus; 
	LocalTime shopOpenTime;
	LocalTime shopCloseTime;
	Boolean tenantOpen;
	java.util.Date  tenantDateTimeCreated; 

}
