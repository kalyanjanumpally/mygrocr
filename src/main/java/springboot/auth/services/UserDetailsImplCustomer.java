package springboot.auth.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework. security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import springboot.auth.entities.EntityCustomerAuth;
import springboot.auth.entities.EntityEmployee;

public class UserDetailsImplCustomer implements UserDetails {
	
	private static final long serialVersionUID = 1L;

	private Integer customerId;
	
	private String firstName;
	
	private String lastName;

	private String email;
	
	private String phoneNo1;
	
	private String phoneNo2;
	
	private String address;
	 
	private String city;
	 
	private String postalCode;
	 
	private String state;

	@JsonIgnore
	private String password;

	private Collection<? extends GrantedAuthority> authorities;

	public UserDetailsImplCustomer(Integer customerId, String firstName, String lastName, String email, String phoneNo1, String phoneNo2, String address,
			String city, String postalCode, String state, String password,
			Collection<? extends GrantedAuthority> authorities) {
		this.customerId = customerId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNo1 = phoneNo1;
		this.phoneNo2 = phoneNo2;
		this.address = address;
		this.city = city;
		this.postalCode = postalCode;
		this.state = state;
		this.password = password;
		this.authorities = authorities;
	}

	public static UserDetailsImplCustomer build(EntityCustomerAuth customer) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
		
		return new UserDetailsImplCustomer(
				customer.getCustomerId(),
				customer.getFirstName(),
				customer.getLastName(),
				customer.getEmail(), 
				customer.getPhoneNo1(),
				customer.getPhoneNo2(),
				customer.getAddress(),
				customer.getCity(),
				customer.getPostalCode(),
				customer.getState(),
				customer.getPassword(), 
				authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public Integer getCustomerId() {
		return customerId;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}	

	@Override
	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getCity() {
		return city;
	}
	
	public String getPhoneNo1() {
		return phoneNo1;
	}
	
	public String getPhoneNo2() {
		return phoneNo2;
	}
	
	public String getState() {
		return state;
	}
	public String getPostalCode() {
		return postalCode;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImplCustomer customer = (UserDetailsImplCustomer) o;
		return Objects.equals(customerId, customer.customerId);
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}
}
