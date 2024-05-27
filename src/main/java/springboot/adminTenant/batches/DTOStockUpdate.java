package springboot.adminTenant.batches;

import java.sql.Date;

import lombok.Data;

@Data
public class DTOStockUpdate {
	
	private Float quantity;
	private String batchNo;
	private Integer batchProductId;
	private Integer batchVariantId;
	private Integer batchProductHomeId;
	private Integer batchVariantHomeId;
	private String batchProductName;
	private String batchUnit;
	private String batchBrandName;	
	private String transactionStatus;
	private Date batchEntryDate;

}
