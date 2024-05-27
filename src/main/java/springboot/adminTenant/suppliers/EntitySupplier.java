package springboot.adminTenant.suppliers;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="suppliers")
public class EntitySupplier {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="supplier_id")
	private int supplierId;
	
	@Column(name="supplier_name")
	private String supplierName;
	
	@Column(name="contact_person")
	private String contactPerson;
	
	@Column(name="phone_no_1")
	private String phoneNo1;
	
	@Column(name="phone_no_2")
	private String phoneNo2;
	
	@Column(name="email")
	private String email;
	
	@Column(name="address")
	private String address;
	
	@JsonIgnore
	@OneToMany(fetch=FetchType.LAZY, cascade= 
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
		 mappedBy="supplier")

	private List<EntityPurchaseInvoiceSuppliers> purchaseInvoices;
	
	//define constructors
	public EntitySupplier() {		
	}

	public EntitySupplier(String supplierName, String contactPerson, String phoneNo1, String phoneNo2, String email,
			String address, List<EntityPurchaseInvoiceSuppliers> purchaseInvoices) {
		this.supplierName = supplierName;
		this.contactPerson = contactPerson;
		this.phoneNo1 = phoneNo1;
		this.phoneNo2 = phoneNo2;
		this.email = email;
		this.address = address;
		this.purchaseInvoices = purchaseInvoices;
	}

	@Override
	public String toString() {
		return "EntitySupplier [supplierId=" + supplierId + ", supplierName=" + supplierName + ", contactPerson="
				+ contactPerson + ", phoneNo1=" + phoneNo1 + ", phoneNo2=" + phoneNo2 + ", email=" + email
				+ ", address=" + address + ", purchaseInvoices=" + purchaseInvoices + "]";
	}

}
