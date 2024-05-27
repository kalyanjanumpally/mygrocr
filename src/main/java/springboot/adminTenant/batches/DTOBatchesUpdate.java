package springboot.adminTenant.batches;

import java.sql.Date;
import java.util.ArrayList;

import lombok.Data;

@Data
public class DTOBatchesUpdate {
	
	ArrayList<DTOStockUpdate> stockUpdateEntries;
	ArrayList<DTOSellingPrice> sellingPriceEntries;
	ArrayList<DTOMrp> mrpEntries;
	

}
