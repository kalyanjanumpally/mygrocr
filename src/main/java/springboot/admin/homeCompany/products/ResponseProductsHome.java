package springboot.admin.homeCompany.products;

import java.util.List;

import lombok.Data;

@Data
public class ResponseProductsHome {
	
	private List<EntityProductHome> products;
	
	private Long countOfProducts;

}

