package springboot.auth.payloads;

import java.util.List;

import lombok.Data;

@Data
public class JwtResponseCustomer {
	
	 private String token;
	 private String type = "Bearer";
	 private Integer id;
	 private String firstName;
	 private String lastName;
	 private String email;
	 private String address;
	 private String city;
	 private String phone1;
	 private String phone2;
	 private String postalCode;
	 private String state;
	 
	 
	public JwtResponseCustomer(String token, Integer id,  String firstName, String lastName, String email, String address, String city,
			String phone1, String phone2, String postalCode, String state) {
		this.token = token;
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.address = address;
		this.city = city;
		this.phone1 = phone1;
		this.phone2 = phone2;
		this.postalCode = postalCode;
		this.state = state;
	}


}
