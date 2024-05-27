package springboot.adminTenant.batches;

import java.sql.Date;
import java.util.Comparator;

import lombok.Data;

@Data
public class DTOVariantBatches {
	
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
	private Boolean display;
	private Float quantity;
	private Float currentQuantity;
	private Float mrp;
	private Float sellingPrice;	
	private Float batchPurchasePrice;
	private Boolean ppIncludesGST;
	private Date expiryDate;	
	private Long batchOrderId;
	private String transactionStatus;
	private Date date;
	
    // Comparator not current used in code
    public static Comparator<DTOVariantBatches> BatchesComparator = new Comparator<DTOVariantBatches>() {
  
        // Comparing attributes
        public int compare(DTOVariantBatches batch1, DTOVariantBatches batch2) {
            
        	Date date1 = batch1.getDate();
        	Date date2 = batch2.getDate();
  
            // Returning in ascending order
            return date2.compareTo(
                       date1);
        }
    };
	
	
	
}
