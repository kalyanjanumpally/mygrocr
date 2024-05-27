package springboot.adminTenant.orders;

import java.util.List;

import lombok.Data;

@Data
public class ResponseOrders {
	
	private List<DtoOrders> dtos;
	
	private Long countOfOrders;

}
