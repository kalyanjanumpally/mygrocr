package springboot.adminTenant.batches;

import java.util.ArrayList;

import lombok.Data;

@Data
public class DTOExpiryBatchData {
	
	Integer productId;
	String productName;
	String brandName;
	String batchNo;
	Float quantity;
	java.sql.Date expiryDate;
}
