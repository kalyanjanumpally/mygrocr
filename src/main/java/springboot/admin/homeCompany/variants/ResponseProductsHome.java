package springboot.admin.homeCompany.variants;

import java.util.List;

import lombok.Data;

@Data
public class ResponseProductsHome {
	
	private List<EntityProductVariantHome> products;
	
	private Long countOfProducts;

}

