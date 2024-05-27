package springboot.admin.homeCompany.categories;

import java.sql.Date;
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


@Getter
@Setter
@Entity
@Indexed
@Table(name="tenants")
public class EntityTenantCategory {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="tenant_id")
	private Integer tenantId;
	
	@Field(termVector = TermVector.YES)
	@Column(name="tenant_name")
	private String tenantName;	
	
	@Column(name="tenant_url")
	private String tenantUrl;	
	/*
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
	
	@Column(name="tenant_open")
	private Boolean tenantOpen;	
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="tenant_date_time_created")
	private java.util.Date tenantDateTimeCreated;
	*/
	
	//define constructors
	public EntityTenantCategory() {		
	}

	@Override
	public String toString() {
		return "EntityTenantCategory [tenantId=" + tenantId + ", tenantName=" + tenantName + ", tenantUrl=" + tenantUrl
				+ "]";
	}

	public EntityTenantCategory(String tenantName, String tenantUrl) {
		super();
		this.tenantName = tenantName;
		this.tenantUrl = tenantUrl;
	}



	
}
