package springboot.adminTenant.suppliers;

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

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="purchase_invoices")
public class EntityPurchaseInvoiceSuppliers {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pur_invoice_id")
	private Long purInvoiceId;
	
	@Column(name="date")
	private Date date;
	
	@Column(name="amount")
	private String amount;
	
	@Column(name="payment_details")
	private String paymentDetails;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade= 
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}
		 )
	@JoinColumn(name="supplier_id")
	private EntitySupplier supplier;
	
	//define constructors
	public EntityPurchaseInvoiceSuppliers() {		
	}

	public EntityPurchaseInvoiceSuppliers(Date date, String amount, String paymentDetails, EntitySupplier supplier) {
		this.date = date;
		this.amount = amount;
		this.paymentDetails = paymentDetails;
		this.supplier = supplier;
	}

	@Override
	public String toString() {
		return "EntityPurchaseInvoice [purInvoiceId=" + purInvoiceId + ", date=" + date + ", amount=" + amount
				+ ", paymentDetails=" + paymentDetails + ", supplier=" + supplier + "]";
	}

}
