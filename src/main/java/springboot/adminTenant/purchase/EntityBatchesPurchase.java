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
@Table(name="batches")
public class EntityBatchesPurchase {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="batch_id")
	private Long batchId;
	
	@Column(name="batch_product_id")
	private Integer batchProductId;
	
	@Column(name="batch_variant_id")
	private Integer batchVariantId;
	
	@Column(name="batch_product_home_id")
	private Integer batchProductHomeId;
	
	@Column(name="batch_variant_home_id")
	private Integer batchVariantHomeId;
	
	@Column(name="batch_product_name")
	private String batchProductName;
	
	@Column(name="batch_unit")
	private String batchUnit;
	
	@Column(name="batch_brand_name")
	private String batchBrandName;
	
	@Column(name="batch_no")
	private String batchNo;
	
	@Column(name="batch_pur_sale_bool")
	private Integer batchPurSaleBool;
	
	@Column(name="quantity")
	private Float quantity;
	
	@Column(name="current_quantity")
	private Float currentQuantity;
	
	@Column(name="batch_MRP")
	private Float mrp;
	
	@Column(name="batch_purchase_price")
	private Float batchPurchasePrice;
	
	@Column(name="pp_includes_gst")
	private Boolean ppIncludesGST;
	
	@Column(name="batch_gst")
	private Integer batchGST;
	
	@Column(name="selling_price")
	private Float sellingPrice;
	
	@Column(name="expiry_date")
	private Date expiryDate;	
	
	@Column(name="batch_purchase_invoice_id")
	private Long batchPurchaseInvoiceId; 
	
	@Column(name="transaction_status")
	private String transactionStatus; 
	
	@Column(name="display")
	private Boolean display; 
	
	
	//define constructors
	public EntityBatchesPurchase() {		
	}


	@Override
	public String toString() {
		return "EntityBatchesPurchase [batchProductId=" + batchProductId + ", batchVariantId=" + batchVariantId
				+ ", batchProductHomeId=" + batchProductHomeId + ", batchVariantHomeId=" + batchVariantHomeId
				+ ", batchProductName=" + batchProductName + ", batchUnit=" + batchUnit + ", batchBrandName="
				+ batchBrandName + ", batchNo=" + batchNo + ", batchPurSaleBool=" + batchPurSaleBool + ", quantity="
				+ quantity + ", currentQuantity=" + currentQuantity + ", mrp=" + mrp + ", batchPurchasePrice="
				+ batchPurchasePrice + ", ppIncludesGST=" + ppIncludesGST + ", batchGST=" + batchGST + ", sellingPrice="
				+ sellingPrice + ", expiryDate=" + expiryDate + ", batchPurchaseInvoiceId=" + batchPurchaseInvoiceId
				+ ", transactionStatus=" + transactionStatus + ", display=" + display + "]";
	}


	public EntityBatchesPurchase(Integer batchProductId, Integer batchVariantId, Integer batchProductHomeId,
			Integer batchVariantHomeId, String batchProductName, String batchUnit, String batchBrandName,
			String batchNo, Integer batchPurSaleBool, Float quantity, Float currentQuantity, Float mrp,
			Float batchPurchasePrice, Boolean ppIncludesGST, Integer batchGST, Float sellingPrice, Date expiryDate,
			Long batchPurchaseInvoiceId, String transactionStatus, Boolean display) {
		super();
		this.batchProductId = batchProductId;
		this.batchVariantId = batchVariantId;
		this.batchProductHomeId = batchProductHomeId;
		this.batchVariantHomeId = batchVariantHomeId;
		this.batchProductName = batchProductName;
		this.batchUnit = batchUnit;
		this.batchBrandName = batchBrandName;
		this.batchNo = batchNo;
		this.batchPurSaleBool = batchPurSaleBool;
		this.quantity = quantity;
		this.currentQuantity = currentQuantity;
		this.mrp = mrp;
		this.batchPurchasePrice = batchPurchasePrice;
		this.ppIncludesGST = ppIncludesGST;
		this.batchGST = batchGST;
		this.sellingPrice = sellingPrice;
		this.expiryDate = expiryDate;
		this.batchPurchaseInvoiceId = batchPurchaseInvoiceId;
		this.transactionStatus = transactionStatus;
		this.display = display;
	}

}
