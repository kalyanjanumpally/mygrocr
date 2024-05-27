package springboot.userHome;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoCustomerHome {
	
	private Integer customerId;
	
	private Boolean customerDeleteStatus;
	
	private String customerType;
	
	private Boolean customerActive;
	
	private String firstName;
	
	private String lastName;
	
	private String companyName;
	
	private String fullName;
	
	private Date dateRegistered;
	
	private String phoneNo1;
	
	private String phoneNo2;
	
	private String email;
	
	private String address;
	
	private String postalCode;
	
	private String city;	

	private String state;

}
