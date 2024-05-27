package springboot.admin.homeCompany.orders;

import java.util.List;

import lombok.Data;

@Data
public class ResponseOrdersHome {
	
	private List<DtoOrderAndBatchesHome> dtos;
	
	private Long countOfOrders;

}
