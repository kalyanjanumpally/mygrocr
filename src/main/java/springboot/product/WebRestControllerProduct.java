package springboot.product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.hibernate.internal.build.AllowSysOut;
import org.hibernate.mapping.Array;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@CrossOrigin 
@RestController
//@RequestMapping("/api/")
public class WebRestControllerProduct {
	
	//quick: inject product DAO (use Constructor injection)
	private WebServiceProduct webServiceProduct;
	private WebServiceSearch searchService;
	
	public WebRestControllerProduct(WebServiceProduct webServiceProduct, WebServiceSearch searchService) {
		this.webServiceProduct = webServiceProduct;
		this.searchService = searchService;
	}
	
	@GetMapping("/")
	public String testApi() {
		return "Connection Successful.";
	}
	
	@GetMapping("/api/products/{productId}/{variantId}")
	public WebDTOProductAndBatches getProduct(@PathVariable Integer productId, @PathVariable Integer variantId) {
					
		return webServiceProduct.findProductAndBatchesById(productId, variantId);		
		
	}
	
	@PostMapping("/api/get-cart-product-variants")
	public WebResponseCartVariants getCartProductVariants(@RequestBody WebDTOCartVariantIds variantIds) {
		
		WebResponseCartVariants variants = webServiceProduct.findCartVariantsAndBatchesByIds(variantIds);		
		
		return variants;
	}
	
	@GetMapping("/api/products-and-batches/{itemsPerPage}/{initialIndex}")
	public WebResponseProducts findAllProductsAndBatches(@PathVariable Integer itemsPerPage, @PathVariable Integer initialIndex) {
		
		WebResponseProducts webResponseProducts = webServiceProduct.findAll(itemsPerPage, initialIndex);
		
		return webResponseProducts;
	}
	
	@GetMapping("/api/search-products-and-batches/{search}/{itemsPerPage}/{startIndex}")
	public WebResponseProducts searchProdsAndBatches(@PathVariable String search,
			@PathVariable Integer startIndex, @PathVariable Integer itemsPerPage) {	
				
		WebResponseProducts searchedProducts = searchService.searchProduct(search, startIndex, itemsPerPage);
		
		return searchedProducts;
	}
	
	@GetMapping("/api/get-products-and-batches-by-category/{categoryId}/{itemsPerPage}/{initialIndex}")
	public WebResponseProducts searchProductsAndBatchesByCategory(@PathVariable Integer categoryId, 
					@PathVariable Integer itemsPerPage, @PathVariable Integer initialIndex) {
		
		return webServiceProduct.getProductsByCategory(categoryId, itemsPerPage, initialIndex);	
	//	List<DTOProductsAndBatches> dTOs = searchService.searchBatches(dbProducts);			
	//	return dTOs;

	}
	
	@GetMapping("/api/get-products-and-batches-by-home-category/{categoryId}/{itemsPerPage}/{initialIndex}")
	public WebResponseProducts searchProductsAndBatchesByHomeCategory(@PathVariable Integer categoryId, 
					@PathVariable Integer itemsPerPage, @PathVariable Integer initialIndex) {
		
		return webServiceProduct.getProductsByHomeCategory(categoryId, itemsPerPage, initialIndex);	
	//	List<DTOProductsAndBatches> dTOs = searchService.searchBatches(dbProducts);			
	//	return dTOs;

	}
	
	@GetMapping("/api/search-products-and-batches-by-brand/{brandId}/{itemsPerPage}/{initialIndex}")
	public WebResponseProducts searchProductsAndBatchesByBrand(@PathVariable Integer brandId,
					@PathVariable Integer itemsPerPage, @PathVariable Integer initialIndex) {
		
		return webServiceProduct.findProductsByBrand(brandId, itemsPerPage, initialIndex);
	}
		
/*		
	@GetMapping("/api/product-batches-quantity-data/{productId}")
	public List<WebEntityBatchesProduct> getBatchesQuantity(@PathVariable Integer productId) {
		
		return searchService.getBatchesQuantity(productId);	
	}
*/	
	
	//add mapping for POST /products - add new product
	
}







