package springboot.userHome;

import java.sql.Date;
import java.util.Comparator;

import lombok.Data;

@Data
public class DtoBatchAndProductVariantUserHome {
	
	private Long batchId;
	private Integer batchProductId;	
	private Integer batchVariantId;
	private Integer batchProductHomeId;
	private Integer batchVariantHomeId;
	private String batchProductName;
	private String batchUnit;
	private String batchBrandName; 	
	private String batchNo;	
	private Integer batchPurSaleBool;	
	private Float quantity;
	private Float mrp;
	private Integer sellingPrice;	
	private Float batchPurchasePrice;
	private Boolean ppIncludesGST;
	private Integer batchGST;
	private Date expiryDate;	
	private Integer batchOrderId;
	private String transactionStatus;
	
}
