package springboot.adminTenant.product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.hibernate.annotations.common.util.impl.LoggerFactory;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@CrossOrigin 
@RestController
@RequestMapping("/api/admin-tenant")
public class RestControllerProduct {
	
	//quick: inject product DAO (use Constructor injection)
	private ServiceProduct serviceProduct;
	private ServiceSearch searchService;
	
	public RestControllerProduct(ServiceProduct serviceProduct, ServiceSearch searchService) {
		this.serviceProduct = serviceProduct;
		this.searchService = searchService;
	}
	
	
	@GetMapping("/get-unlisted-products/{startIndex}/{itemsPerPage}")
	public ResponseProducts getUnlistedProducts(@PathVariable Integer startIndex, @PathVariable Integer itemsPerPage) throws Exception {
		
		ResponseProducts responseProducts = serviceProduct.findUnlistedProducts(startIndex, itemsPerPage);	
			
		if(responseProducts.getCountOfProducts() == 0) {
			return null;
		} 
		else {			
			return responseProducts;
		}		
	}
	
	
	@GetMapping("/get-unlisted-products-from-bulk-list/{startIndex}/{itemsPerPage}")
	public ResponseProducts getUnlistedProductsFromBulkList(@PathVariable Integer startIndex, @PathVariable Integer itemsPerPage) throws Exception {
		
		ResponseProducts responseProducts = serviceProduct.findUnlistedProductsFromBulkList(startIndex, itemsPerPage);		
		if(responseProducts.getCountOfProducts() == 0) {
			return null;
		} 
		else {			
			return responseProducts;
		}		
	}
	
	
	@GetMapping("/get-unlisted-products-in-brand/{brandId}/{startIndex}/{itemsPerPage}")
	public ResponseProducts getUnlistedProductsInBrand(@PathVariable Integer brandId, @PathVariable Integer startIndex, @PathVariable Integer itemsPerPage) throws Exception {
		
		ResponseProducts responseProducts = serviceProduct.findUnlistedProductsInBrand(brandId, startIndex, itemsPerPage);		

		return responseProducts;
	}
	
	@GetMapping("/search-unlisted-products-by-name/{searchTerm}/{startIndex}/{itemsPerPage}")
	public ResponseProducts searchUnlistedProductsByName(@PathVariable String searchTerm, @PathVariable Integer startIndex, @PathVariable Integer itemsPerPage) throws Exception {
		
		ResponseProducts responseProducts = serviceProduct.searchUnlistedProductsByName(searchTerm, startIndex, itemsPerPage);		
		
			return responseProducts;
				
	}	
	

/*	@GetMapping("/products")
	public List<EntityProduct> findAll() {
		
		List<EntityProduct> dbProducts = serviceProduct.findAll();		
		return dbProducts;	
	} */
	
	
	@GetMapping("/get-products/{itemsPerPage}/{initialIndex}")
	public ResponseProducts getProducts(@PathVariable Integer itemsPerPage, @PathVariable Integer initialIndex) {
		
		ResponseProducts responseProducts = serviceProduct.getProducts(itemsPerPage, initialIndex);
		
		return responseProducts;
	}
	
	@GetMapping("/get-products-by-category/{categoryId}/{itemsPerPage}/{initialIndex}")
	public ResponseProducts getProductsByCategory(@PathVariable Integer categoryId, @PathVariable Integer itemsPerPage, @PathVariable Integer initialIndex) {
		
		ResponseProducts responseProducts = serviceProduct.getProductsByCategory(categoryId, itemsPerPage, initialIndex);
		
		return responseProducts;
	}
	
	@GetMapping("/get-products-by-brand/{brandId}/{itemsPerPage}/{initialIndex}")
	public ResponseProducts getProductsByBrand(@PathVariable Integer brandId, @PathVariable Integer itemsPerPage, @PathVariable Integer initialIndex) {
		
		ResponseProducts responseProducts = serviceProduct.getProductsByBrand(brandId, itemsPerPage, initialIndex);
		
		return responseProducts;
	}
	
/*	
	@GetMapping("/search-product/{search}")
	public List<EntityProduct> searchProd(@PathVariable String search) {
		
		List<EntityProduct> dbProducts = searchService.searchProduct(search);						
		return dbProducts;	
	}
*/

	
	@GetMapping("/products-and-batches/{itemsPerPage}/{initialIndex}")
	public ResponseProductsWithBatches findAllProductsAndBatches(@PathVariable Integer itemsPerPage, @PathVariable Integer initialIndex) {
		
		ResponseProductsWithBatches responseProducts = serviceProduct.findAllProductsAndBatches(itemsPerPage, initialIndex);
		
		return responseProducts;
	}
	
	@GetMapping("/get-products-and-batches-by-category/{categoryId}/{itemsPerPage}/{initialIndex}")
	public ResponseProductsWithBatches searchProductsAndBatchesByCategory(@PathVariable Integer categoryId, 
					@PathVariable Integer itemsPerPage, @PathVariable Integer initialIndex) {
		
		return serviceProduct.findProductsAndBatchesByCategory(categoryId, itemsPerPage, initialIndex);	
	//	List<DTOProductsAndBatches> dTOs = searchService.searchBatches(dbProducts);			
	//	return dTOs;

	}
	

	/*
	@GetMapping("/get-products-and-batches-by-home-category/{categoryId}/{itemsPerPage}/{initialIndex}")
	public ResponseProductsWithBatches searchProductsAndBatchesByHomeCategory(@PathVariable Integer categoryId, 
					@PathVariable Integer itemsPerPage, @PathVariable Integer initialIndex) {
		
		return serviceProduct.findProductsAndBatchesByHomeCategory(categoryId, itemsPerPage, initialIndex);	
	//	List<DTOProductsAndBatches> dTOs = searchService.searchBatches(dbProducts);			
	//	return dTOs;

	}
	*/
	
	
	
	
	/*
	@GetMapping("/get-products-and-batches-by-brand/{brandId}/{itemsPerPage}/{initialIndex}")
	public ResponseProductsWithBatches searchProductsAndBatchesByBrand(@PathVariable Integer brandId,
					@PathVariable Integer itemsPerPage, @PathVariable Integer initialIndex) {
		
		return serviceProduct.findProductsAndBatchesByBrand(brandId, itemsPerPage, initialIndex);
	}
	*/
		
	
	@GetMapping("/search-product-by-name/{search}/{itemsPerPage}/{startIndex}")
	public ResponseProducts searchProductByName(@PathVariable String search,
			@PathVariable Integer startIndex, @PathVariable Integer itemsPerPage) {	
				
		return searchService.searchProduct(search, startIndex, itemsPerPage);			
	}
	
	@GetMapping("/search-product-and-batches-by-name/{search}/{itemsPerPage}/{startIndex}")
	public ResponseProductsWithBatches searchProductsWithBatchesByName(@PathVariable String search,
			@PathVariable Integer startIndex, @PathVariable Integer itemsPerPage) {	
				
		return searchService.searchProductWithBatches(search, startIndex, itemsPerPage);			
	}
	

	@PostMapping("/add-new-products")
	public void addNewProducts( @RequestBody List<EntityProduct> products, @RequestHeader("tenant-url") String tenantUrl) throws Exception {
		
		serviceProduct.saveProducts(products, tenantUrl);	

	}

	
	@PostMapping("/add-bulk-products")
	public void addBulkProducts(@RequestHeader("tenant-url") String tenantUrl) throws Exception {
		
		serviceProduct.addBulkProducts(tenantUrl);	

	}	
	//add mapping for PUT /products - update existing product display
	
	@PutMapping("/variant-toggle-display")
	public void updateProductDisplay(@RequestBody EntityProductVariantByUnitProduct variant) {
		
		serviceProduct.updateVariantDisplay(variant);		
	}
	
	
	@PutMapping("/toggle-display-out-of-stock-variant-url")
	public void updateDisplayOutOfStockVariant(@RequestBody EntityProductVariantByUnitProduct variant) {
		
		serviceProduct.updateDisplayOutOfStockVariant(variant);		
	}
		
	/*
	@PostMapping("/products")
	public EntityProduct addProduct(@RequestBody EntityProduct theProduct) {
		theProduct.setProductId(0);		
		EntityProduct dbProduct = serviceProduct.save(theProduct);	
		return dbProduct;
	}
	*/
	
	/*
	@GetMapping("/products/{productId}")
	public DTOProductsAndBatches getProductAndBatches(@PathVariable Integer productId) {
					
		DTOProductsAndBatches theProduct = serviceProduct.findProductAndBatchesById(productId);		
		if(theProduct == null) {			
			return null;
		} 
		else {
			return theProduct;
		}		
	}
	*/
	

	
}







