package springboot.adminTenant.orders;

import java.util.List;

import lombok.Data;

@Data
public class DtoOrders {
	
	private EntityOrder order;
//	private List<DtoBatchAndProductVariant> batches;
	
	private List<EntityBatchesOrders> batches;

}

