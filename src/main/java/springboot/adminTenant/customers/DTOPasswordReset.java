package springboot.adminTenant.customers;

import lombok.Data;

@Data
public class DTOPasswordReset {
	
	private int customerId;
	
	private String newPassword;

}
