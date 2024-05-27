package springboot.admin.homeCompany.products;

import lombok.Data;

@Data
public class DTOProduct {
	
	EntityProductHome product;
	Integer countOfTenants;

}
