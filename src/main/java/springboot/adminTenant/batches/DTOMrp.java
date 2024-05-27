package springboot.adminTenant.batches;

import java.sql.Date;

import lombok.Data;

@Data
public class DTOMrp {
	
	private String batchNo;
	private Integer batchProductId;
	private Integer batchVariantId;
	private Integer batchProductHomeId;
	private Integer batchVariantHomeId;
	private Float mrp;

}
