package springboot.admin.homeCompany.tenants;

import java.sql.Date;
import java.time.LocalTime;
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
import org.springframework.boot.autoconfigure.domain.EntityScan;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import springboot.admin.homeCompany.customers.EntityCustomerHome;

@Getter
@Setter
@Entity
@Indexed
@Table(name="tenants")
public class EntityTenant {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="tenant_id")
	private Integer tenantId;
	
	@Field(termVector = TermVector.YES)
	@Column(name="tenant_name")
	private String tenantName;	
	
	@Field(termVector = TermVector.YES)
	@Column(name="tenant_url")
	private String tenantUrl;
	
	//@Column(name="display_out_of_stock_products_bool")
	//private Boolean displayOutOfStockProductsBool;	
	
	@Column(name="tenant_image_url")
	private String tenantImageUrl;
	
	@Column(name="shop_open_time")
	private LocalTime shopOpenTime;
	
	@Column(name="shop_close_time")
	private LocalTime shopCloseTime;
	
	@Column(name="tenant_contact_person")
	private String tenantContactPerson;
	
	@Column(name="tenant_phone_number_1")
	private String tenantPhoneNumber1;
	
	@Column(name="tenant_phone_number_2")
	private String tenantPhoneNumber2;
		
	@Column(name="tenant_address")
	private String tenantAddress;
	
	@Column(name="tenant_area")
	private String tenantArea;
	
	@Column(name="tenant_city")
	private String tenantCity;
	
	@Column(name="tenant_postal_code")
	private String tenantPostalCode;
	
	@Column(name="tenant_email")
	private String tenantEmail;	
	
	@Column(name="tenant_active")
	private Boolean tenantActive;
	
	@Column(name="tenant_delete_status")
	private Boolean tenantDeleteStatus;	
	
	@Column(name="tenant_open")
	private Boolean tenantOpen;	
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="tenant_date_time_created")
	private java.util.Date tenantDateTimeCreated;
	
	@JsonIgnore
	@ManyToMany(fetch=FetchType.LAZY, cascade= 
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
		mappedBy="tenants")

	private List<EntityCustomerHome> customers;
	
	//define constructors
	public EntityTenant() {		
	}

	@Override
	public String toString() {
		return "EntityTenant [tenantId=" + tenantId + ", tenantName=" + tenantName + ", tenantUrl=" + tenantUrl
				+ ", tenantImageUrl="
				+ tenantImageUrl + ", shopOpenTime=" + shopOpenTime + ", shopCloseTime=" + shopCloseTime
				+ ", tenantContactPerson=" + tenantContactPerson + ", tenantPhoneNumber1=" + tenantPhoneNumber1
				+ ", tenantPhoneNumber2=" + tenantPhoneNumber2 + ", tenantAddress=" + tenantAddress + ", tenantArea="
				+ tenantArea + ", tenantCity=" + tenantCity + ", tenantPostalCode=" + tenantPostalCode
				+ ", tenantEmail=" + tenantEmail + ", tenantActive=" + tenantActive + ", tenantDeleteStatus="
				+ tenantDeleteStatus + ", tenantOpen=" + tenantOpen + ", tenantDateTimeCreated=" + tenantDateTimeCreated
				+ ", customers=" + customers + "]";
	}

	public EntityTenant(String tenantName, String tenantUrl,
			String tenantImageUrl, LocalTime shopOpenTime, LocalTime shopCloseTime, String tenantContactPerson,
			String tenantPhoneNumber1, String tenantPhoneNumber2, String tenantAddress, String tenantArea,
			String tenantCity, String tenantPostalCode, String tenantEmail, Boolean tenantActive,
			Boolean tenantDeleteStatus, Boolean tenantOpen, java.util.Date tenantDateTimeCreated,
			List<EntityCustomerHome> customers) {
		super();
		this.tenantName = tenantName;
		this.tenantUrl = tenantUrl;
		this.tenantImageUrl = tenantImageUrl;
		this.shopOpenTime = shopOpenTime;
		this.shopCloseTime = shopCloseTime;
		this.tenantContactPerson = tenantContactPerson;
		this.tenantPhoneNumber1 = tenantPhoneNumber1;
		this.tenantPhoneNumber2 = tenantPhoneNumber2;
		this.tenantAddress = tenantAddress;
		this.tenantArea = tenantArea;
		this.tenantCity = tenantCity;
		this.tenantPostalCode = tenantPostalCode;
		this.tenantEmail = tenantEmail;
		this.tenantActive = tenantActive;
		this.tenantDeleteStatus = tenantDeleteStatus;
		this.tenantOpen = tenantOpen;
		this.tenantDateTimeCreated = tenantDateTimeCreated;
		this.customers = customers;
	}


}
