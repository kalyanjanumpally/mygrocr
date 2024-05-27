package springboot.adminTenant.purchase;

import java.util.List;

import lombok.Data;

@Data
public class DtoPurchase {
	
	private EntityPurchaseInvoice purchaseInvoice;
//	private List<DtoBatchAndProductPurchase> batches;
	
	private List<EntityBatchesPurchase> batches;

}
