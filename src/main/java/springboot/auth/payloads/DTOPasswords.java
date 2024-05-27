package springboot.auth.payloads;

import lombok.Data;

@Data
public class DTOPasswords {
	
	private Integer customerId;
	
	private String email;
	
	private String oldPassword;
	
	private String newPassword;
	
	private String confirmPassword;
	
	
}


