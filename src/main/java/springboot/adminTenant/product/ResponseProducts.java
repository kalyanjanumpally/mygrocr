package springboot.adminTenant.product;

import java.util.List;

import lombok.Data;

@Data
public class ResponseProducts {
	
	private List<EntityProduct> dtos;
	
	private Long countOfProducts;

}
