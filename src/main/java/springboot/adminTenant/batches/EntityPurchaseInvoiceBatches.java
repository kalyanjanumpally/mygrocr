package springboot.adminTenant.batches;

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

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="purchase_invoices")
public class EntityPurchaseInvoiceBatches {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pur_invoice_id")
	private Long purchaseInvoiceId;
	
	@Column(name="date_created")
	private Date date;
	
	@Column(name="amount")
	private String amount;
	
	@Column(name="payment_details")
	private String paymentDetails;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade= 
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}
		 )
	@JoinColumn(name="supplier_id")
	private EntitySupplierBatches supplier;
	
	
/*	@JsonIgnore
	@OneToMany(fetch=FetchType.LAZY, cascade= 
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
		 mappedBy="purchaseInvoice")

	private List<EntityBatches> batches; */
	
	
	
	//define constructors
	public EntityPurchaseInvoiceBatches() {		
	}

	public EntityPurchaseInvoiceBatches(Date date, String amount, String paymentDetails, EntitySupplierBatches supplier) {
		this.date = date;
		this.amount = amount;
		this.paymentDetails = paymentDetails;
		this.supplier = supplier;
	}

	@Override
	public String toString() {
		return "EntityPurchaseInvoice [purInvoiceId=" + purchaseInvoiceId + ", date=" + date + ", amount=" + amount
				+ ", paymentDetails=" + paymentDetails + ", supplier=" + supplier + "]";
	}

}
