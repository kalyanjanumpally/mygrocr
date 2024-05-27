package springboot.product;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface WebDAOProducts {
	
	public List<WebEntityProduct> findAll(Integer itemsPerPage, Integer initialIndex);
	
	public WebEntityProduct findById(Integer theId, Integer variantId);

	public List<WebEntityProduct> getProductsByCategory(Integer categoryId, Integer itemsPerPage, Integer initialIndex);

	public List<WebEntityProduct> findProductsByBrand(Integer brandId, Integer itemsPerPage, Integer initialIndex);

	Long countOfAllProducts();

	public Long countOfAllProductsByCategory(Integer categoryId);

	public Long countOfAllProductsByBrand(Integer brandId);

	public List<WebEntityProduct> findCartProductsByIds(WebDTOCartVariantIds productIds);

	public List<WebEntityProductVariantByUnit> getCartVariants(WebDTOCartVariantIds variantIds);

	public List<WebEntityProduct> getProductsByHomeCategory(Integer categoryId, Integer itemsPerPage,
			Integer initialIndex);



}
