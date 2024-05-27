package springboot.admin.homeCompany.orders;

import java.util.List;

import lombok.Data;

@Data
public class DtoOrderAndBatchesHome {
	
	private DtoOrderHome order;
	private List<DtoBatchAndProductHome> batches;
//	private List<EntityCustomerPaymentsOrders> payments;

}

