package springboot.userHome;

import java.util.List;

import lombok.Data;

@Data
public class ResponseOrdersUserHome {
	
	private List<DtoOrdersAndBatchesUserHome> dtos;
	
	private Long countOfOrders;

}
