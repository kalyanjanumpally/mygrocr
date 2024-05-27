package springboot.admin.homeCompany.customers;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.TermVector;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import springboot.admin.homeCompany.orders.EntityOrderHome;


@Getter
@Setter
@Entity
@Indexed
@Table(name="customers")
public class EntityCustomerHome {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="customer_id")
	private Integer customerId;
	
	@Column(name="customer_delete_status")
	private Boolean customerDeleteStatus;
	
	@Column(name="customer_type")
	private String customerType;
	
	@Column(name="customer_active")
	private Boolean customerActive;
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="last_name")
	private String lastName;
	
	@Column(name="company_name")
	@Field(termVector = TermVector.YES)
	private String companyName;
	
	@Column(name="full_name")
	@Field(termVector = TermVector.YES)
	private String fullName;
	
	@CreationTimestamp
	@Temporal(TemporalType.DATE)
	@Column(name="date_registered")
	private Date dateRegistered;
	
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
	
	@ManyToMany(fetch=FetchType.LAZY, cascade= 
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(
			name="customer_tenant",
			joinColumns=@JoinColumn(name="customer_id"),
			inverseJoinColumns=@JoinColumn(name="tenant_id")		
			)
	private List<EntityTenantCustomer> tenants;
	
	
	@JsonIgnore
	@OneToMany(fetch=FetchType.EAGER, cascade= 
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
		 mappedBy="customer")
	private List<EntityOrderHome> orders;
	
	//define constructors
	public EntityCustomerHome() {		
	}

	public EntityCustomerHome(Boolean customerDeleteStatus, String customerType, Boolean customerActive,
			String firstName, String lastName, String companyName, String fullName, Date dateRegistered,
			String phoneNo1, String phoneNo2, String email, String address, String postalCode, String city,
			String state, List<EntityTenantCustomer> tenants, List<EntityOrderHome> orders) {
		super();
		this.customerDeleteStatus = customerDeleteStatus;
		this.customerType = customerType;
		this.customerActive = customerActive;
		this.firstName = firstName;
		this.lastName = lastName;
		this.companyName = companyName;
		this.fullName = fullName;
		this.dateRegistered = dateRegistered;
		this.phoneNo1 = phoneNo1;
		this.phoneNo2 = phoneNo2;
		this.email = email;
		this.address = address;
		this.postalCode = postalCode;
		this.city = city;
		this.state = state;
		this.tenants = tenants;
		this.orders = orders;
	}

	@Override
	public String toString() {
		return "EntityCustomerHome [customerId=" + customerId + ", customerDeleteStatus=" + customerDeleteStatus
				+ ", customerType=" + customerType + ", customerActive=" + customerActive + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", companyName=" + companyName + ", fullName=" + fullName
				+ ", dateRegistered=" + dateRegistered + ", phoneNo1=" + phoneNo1 + ", phoneNo2=" + phoneNo2
				+ ", email=" + email + ", address=" + address + ", postalCode=" + postalCode + ", city=" + city
				+ ", state=" + state + ", tenants=" + tenants + ", orders=" + orders + "]";
	}
	
}
