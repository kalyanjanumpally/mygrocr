package springboot.auth.payloads;

import java.util.Set;
import javax.validation.constraints.*;

import lombok.Data;

@Data
public class SignupRequest {
	  @Size(min = 3, max = 20)
	  private String username;
	  
	  private String tenant;

	  private Set<String> role;
	  
	  @Size(min = 6, max = 40)
	  private String password;

}
