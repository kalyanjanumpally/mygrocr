package springboot.adminTenant.batches;

import java.sql.Date;

import lombok.Data;

@Data
public class DTOSellingPrice {
	
	private String batchNo;
	private Integer batchProductId;
	private Integer batchVariantId;
	private Integer batchProductHomeId;
	private Integer batchVariantHomeId;
	private Float sellingPrice;

}
