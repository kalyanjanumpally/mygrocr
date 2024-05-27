package springboot.adminTenant.product;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class DTOProductsAndBatches {
	
	private EntityProduct product;
	
//	private List<DTOBatchData> batchesData;	
	private List<EntityBatchesProduct> batchesData;
	

}
