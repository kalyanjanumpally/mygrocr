package springboot.admin.homeCompany.customers;

import lombok.Data;

@Data
public class DTOPasswordResetHome {
	
	private int customerId;
	
	private String newPassword;

}
