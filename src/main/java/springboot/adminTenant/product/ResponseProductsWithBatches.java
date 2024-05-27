package springboot.adminTenant.product;

import java.util.List;

import lombok.Data;

@Data
public class ResponseProductsWithBatches {
	
	private List<DTOProductsAndBatches> dtos;
	
	private Long countOfProducts;

}
