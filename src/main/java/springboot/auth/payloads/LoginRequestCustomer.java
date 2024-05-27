package springboot.auth.payloads;

import lombok.Data;

@Data
public class LoginRequestCustomer {
	
  private String email;

  private String password;


}
