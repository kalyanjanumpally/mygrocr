package springboot.adminTenant.purchase;

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

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="purchase_invoices")
public class EntityPurchaseInvoice {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pur_invoice_id")
	private Long purchaseInvoiceId;
	
	@Column(name="pur_return_id")
	private Long purReturnId;
	
	@Column(name="number_of_batches")
	private Integer numberOfBatches;	
	
	@Column(name="date_created")
	private Date dateCreated;
	
	@Column(name="amount")
	private Float amount;
	
	@Column(name="purchase_delivery_status")
	private String purchaseDeliveryStatus;	
	

	
	@ManyToOne(fetch=FetchType.EAGER, cascade= 
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}
		 )
	@JoinColumn(name="supplier_id")
	private EntitySupplierPurchase supplier;
	
	//define constructors
	public EntityPurchaseInvoice() {		
	}

	@Override
	public String toString() {
		return "EntityPurchaseInvoice [purchaseInvoiceId=" + purchaseInvoiceId + ", purReturnId=" + purReturnId
				+ ", numberOfBatches=" + numberOfBatches + ", dateCreated=" + dateCreated + ", amount=" + amount
				+ ", purchaseDeliveryStatus=" + purchaseDeliveryStatus + ", supplier=" + supplier + "]";
	}

	public EntityPurchaseInvoice(Long purReturnId, Integer numberOfBatches, Date dateCreated, Float amount,
			String purchaseDeliveryStatus, EntitySupplierPurchase supplier) {
		super();
		this.purReturnId = purReturnId;
		this.numberOfBatches = numberOfBatches;
		this.dateCreated = dateCreated;
		this.amount = amount;
		this.purchaseDeliveryStatus = purchaseDeliveryStatus;
		this.supplier = supplier;
	}




}
