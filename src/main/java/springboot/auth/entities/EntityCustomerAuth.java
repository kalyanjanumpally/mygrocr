package springboot.auth.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.TermVector;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name="customers")
public class EntityCustomerAuth {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="customer_id")
	private int customerId;
	
	@NotBlank
	//@JsonIgnore
	@Size(max = 200)
	@Column(name="password")
	private String password;
	
	@NotBlank
	@Size(max = 50)
	@Column(name="first_name")
	private String firstName;
	
	@Size(max = 50)
	@Column(name="last_name")
	private String lastName;
	
	@Size(max = 100)
	@Column(name="full_name")
	@Field(termVector = TermVector.YES)
	private String fullName;
	
	@Size(max = 100)
	@Column(name="company_name")
	@Field(termVector = TermVector.YES)
	private String companyName;	
	
	@Column(name="customer_type")
	private String customerType;
	
	@Column(name="customer_active")
	private Boolean customerActive;
		
	@CreationTimestamp
	@Temporal(TemporalType.DATE)
	@Column(name="date_registered")
	private Date dateRegistered;
	
	@NotBlank
	@Size(max = 10)
	@Column(name="phone_no_1")
	private String phoneNo1;
	
	@Size(max = 10)
	@Column(name="phone_no_2")
	private String phoneNo2;
	
	@NotBlank
	@Size(max = 50)
	@Email
	@Column(name="email")
	private String email;
	
	@NotBlank
	@Size(max = 1000)
	@Column(name="address")
	private String address;
	
	@NotBlank
	@Size(max = 7)
	@Column(name="postal_code")
	private String postalCode;
	
	
	@Size(max = 5)
	@Column(name="city")
	private String city;	
	
	
	@Size(max = 20)
	@Column(name="state")
	private String state;
	
	
	public EntityCustomerAuth() {
		
	}


	@Override
	public String toString() {
		return "EntityCustomerAuth [customerId=" + customerId + ", password=" + password + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", fullName=" + fullName + ", companyName=" + companyName
				+ ", customerType=" + customerType + ", customerActive=" + customerActive + ", dateRegistered="
				+ dateRegistered + ", phoneNo1=" + phoneNo1 + ", phoneNo2=" + phoneNo2 + ", email=" + email
				+ ", address=" + address + ", postalCode=" + postalCode + ", city=" + city + ", state=" + state + "]";
	}


	public EntityCustomerAuth(@NotBlank @Size(max = 200) String password, @NotBlank @Size(max = 50) String firstName,
			@Size(max = 50) String lastName, @Size(max = 100) String fullName, @Size(max = 100) String companyName,
			String customerType, Boolean customerActive, Date dateRegistered, @NotBlank @Size(max = 10) String phoneNo1,
			@Size(max = 10) String phoneNo2, @NotBlank @Size(max = 50) @Email String email,
			@NotBlank @Size(max = 1000) String address, @NotBlank @Size(max = 7) String postalCode,
			@Size(max = 5) String city, @Size(max = 20) String state) {
		super();
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.fullName = fullName;
		this.companyName = companyName;
		this.customerType = customerType;
		this.customerActive = customerActive;
		this.dateRegistered = dateRegistered;
		this.phoneNo1 = phoneNo1;
		this.phoneNo2 = phoneNo2;
		this.email = email;
		this.address = address;
		this.postalCode = postalCode;
		this.city = city;
		this.state = state;
	}


}
