package springboot.adminTenant.product;

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
public class EntityBatchesProduct {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="batch_id")
	private Long batchId;
	
	@Column(name="batch_order_id")
	private Long batchOrderId;
	
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
	
	@Column(name="display")
	private Boolean display;
	
	@Column(name="transaction_status")
	private String transactionStatus;
	
	@Column(name="quantity")
	private Float quantity;
	
	@Column(name="current_quantity")
	private Float currentQuantity;
	
	@Column(name="batch_MRP")
	private Float mrp;
	
	@Column(name="selling_price")
	private Float sellingPrice;
	
	@Column(name="batch_purchase_price")
	private Float batchPurchasePrice;
	
	@Column(name="pp_includes_gst")
	private Boolean ppIncludesGST;
	
	@Column(name="batch_gst")
	private Integer batchGST;	
	
	@Column(name="expiry_date")
	private Date expiryDate;	
	
	/*
	@ManyToOne(fetch=FetchType.LAZY, cascade= 
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}
		 )
	@JoinColumn(name="batch_purchase_invoice_id")
	private EntityPurchaseInvoiceBatches purchaseInvoice; */
	
	//define constructors
	public EntityBatchesProduct() {		
	}

	@Override
	public String toString() {
		return "EntityBatchesProduct [batchId=" + batchId + ", batchOrderId=" + batchOrderId + ", batchProductId="
				+ batchProductId + ", batchVariantId=" + batchVariantId + ", batchProductHomeId=" + batchProductHomeId
				+ ", batchVariantHomeId=" + batchVariantHomeId + ", batchProductName=" + batchProductName
				+ ", batchUnit=" + batchUnit + ", batchBrandName=" + batchBrandName + ", batchNo=" + batchNo
				+ ", batchPurSaleBool=" + batchPurSaleBool + ", display=" + display + ", transactionStatus="
				+ transactionStatus + ", quantity=" + quantity + ", currentQuantity=" + currentQuantity + ", mrp=" + mrp
				+ ", sellingPrice=" + sellingPrice + ", batchPurchasePrice=" + batchPurchasePrice + ", ppIncludesGST="
				+ ppIncludesGST + ", batchGST=" + batchGST + ", expiryDate=" + expiryDate + "]";
	}

	public EntityBatchesProduct(Long batchOrderId, Integer batchProductId, Integer batchVariantId,
			Integer batchProductHomeId, Integer batchVariantHomeId, String batchProductName, String batchUnit,
			String batchBrandName, String batchNo, Integer batchPurSaleBool, Boolean display, String transactionStatus,
			Float quantity, Float currentQuantity, Float mrp, Float sellingPrice, Float batchPurchasePrice,
			Boolean ppIncludesGST, Integer batchGST, Date expiryDate) {
		super();
		this.batchOrderId = batchOrderId;
		this.batchProductId = batchProductId;
		this.batchVariantId = batchVariantId;
		this.batchProductHomeId = batchProductHomeId;
		this.batchVariantHomeId = batchVariantHomeId;
		this.batchProductName = batchProductName;
		this.batchUnit = batchUnit;
		this.batchBrandName = batchBrandName;
		this.batchNo = batchNo;
		this.batchPurSaleBool = batchPurSaleBool;
		this.display = display;
		this.transactionStatus = transactionStatus;
		this.quantity = quantity;
		this.currentQuantity = currentQuantity;
		this.mrp = mrp;
		this.sellingPrice = sellingPrice;
		this.batchPurchasePrice = batchPurchasePrice;
		this.ppIncludesGST = ppIncludesGST;
		this.batchGST = batchGST;
		this.expiryDate = expiryDate;
	}

	
}
