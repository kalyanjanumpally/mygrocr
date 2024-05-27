package springboot.adminTenant.orders;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.TermVector;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Indexed
@Table(name="customers")
public class EntityCustomersOrders {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="customer_id")
	private Integer customerId;
	
	@Column(name="customer_home_id")
	private Integer customerHomeId;	
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="last_name")
	private String lastName;
	
	@Column(name="full_name")
	@Field(termVector = TermVector.YES)
	private String fullName;
	
	@Column(name="company_name")
	@Field(termVector = TermVector.YES)
	private String companyName;
	
	@Column(name="customer_type")
	private String customerType;
	
	@Column(name="customer_active")
	private Boolean customerActive;
	
	@Column(name="phone_no_1")
	private String phoneNo1;
	
	@Column(name="phone_no_2")
	private String phoneNo2;
	
	@Column(name="email")
	private String email;
	
	@Column(name="address")
	private String address;
	
	@Column(name="postal_code")
	private String postalCode;
	
	@Column(name="city")
	private String city;	
	
	@Column(name="state")
	private String state;
	
	@JsonIgnore
	@OneToMany(fetch=FetchType.EAGER, cascade= 
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
		 mappedBy="customer")

	private List<EntityOrder> orders;
	
	//define constructors
	public EntityCustomersOrders() {		
	}

	public EntityCustomersOrders(Integer customerHomeId, String firstName, String lastName, String fullName,
			String companyName, String customerType, Boolean customerActive, String phoneNo1, String phoneNo2,
			String email, String address, String postalCode, String city, String state, List<EntityOrder> orders) {
		super();
		this.customerHomeId = customerHomeId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.fullName = fullName;
		this.companyName = companyName;
		this.customerType = customerType;
		this.customerActive = customerActive;
		this.phoneNo1 = phoneNo1;
		this.phoneNo2 = phoneNo2;
		this.email = email;
		this.address = address;
		this.postalCode = postalCode;
		this.city = city;
		this.state = state;
		this.orders = orders;
	}

	@Override
	public String toString() {
		return "EntityCustomersOrders [customerId=" + customerId + ", customerHomeId=" + customerHomeId + ", firstName="
				+ firstName + ", lastName=" + lastName + ", fullName=" + fullName + ", companyName=" + companyName
				+ ", customerType=" + customerType + ", customerActive=" + customerActive + ", phoneNo1=" + phoneNo1
				+ ", phoneNo2=" + phoneNo2 + ", email=" + email + ", address=" + address + ", postalCode=" + postalCode
				+ ", city=" + city + ", state=" + state + ", orders=" + orders + "]";
	}



}
