package springboot.auth.payloads;

import lombok.Data;

@Data
public class DTOPasswordTenantEmployees {
	
	private String username;
	
	private String tenant;
	
	private String oldPassword;
	
	private String newPassword;
	
	private String confirmPassword;
	
	
}


