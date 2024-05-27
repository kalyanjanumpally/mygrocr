package springboot.userHome;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoOnlineOrderDataUserHome {
	
	private DtoOrderUserHome order;
	private List<DtoBatchAndProductVariantUserHome> batches;
	private DtoCustomerHome dtoCustomer;
	private Integer tenantId;
	
//	private List<EntityCustomerPaymentsOrders> payments;

}

