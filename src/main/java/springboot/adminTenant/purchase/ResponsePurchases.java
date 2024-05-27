package springboot.adminTenant.purchase;

import java.util.List;

import lombok.Data;

@Data
public class ResponsePurchases {
	
	private List<DtoPurchase> dtos;
	
	private Long countOfPurchases;

}
