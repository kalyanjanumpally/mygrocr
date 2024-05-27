package springboot.auth.payloads;

import lombok.Data;

@Data
public class LoginRequest {
	
  private String username;

  private String password;


}
