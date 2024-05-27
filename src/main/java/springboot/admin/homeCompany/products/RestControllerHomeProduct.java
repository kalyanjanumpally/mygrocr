package springboot.admin.homeCompany.products;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




@CrossOrigin
@RestController
@RequestMapping("/api/admin")
public class RestControllerHomeProduct {
	
	private ServiceHomeProduct serviceProduct;
	
	@Autowired
	public RestControllerHomeProduct(ServiceHomeProduct theServiceHomeCompanyProduct) {
		serviceProduct = theServiceHomeCompanyProduct;		
	}
	
	//add mapping for POST /products - add new product
	
	@PostMapping("/add-new-product")
	public EntityProductHome addProduct(@RequestBody EntityProductHome theProduct) throws Exception {
		theProduct.setProductId(0);	
		
		for(EntityProductVariantByUnitProductHome variant : theProduct.getProductVariantsByUnit()) {
			variant.setVariantId(0);
		}
		
		EntityProductHome dbProduct = serviceProduct.save(theProduct);	
		return dbProduct;
	}
	
	@GetMapping("/get-products/{itemsPerPage}/{initialIndex}")
	public ResponseProductsHome findAllProductsAndBatches(@PathVariable Integer itemsPerPage, @PathVariable Integer initialIndex) {
		
		ResponseProductsHome responseProducts = serviceProduct.findAll(itemsPerPage, initialIndex);
		
		return responseProducts;
	}
	
	@PutMapping("/product-active-status-toggle/{productId}")
	public void productActiveStatusToggle(@PathVariable Integer productId) {
		
		serviceProduct.productActiveStatusToggle(productId);			
	}
	
	
	@GetMapping("/get-product/{productId}")
	public DTOProduct getProduct(@PathVariable Integer productId) {
					
		DTOProduct dTOProduct = serviceProduct.findById(productId);		
		return dTOProduct;
	}
	
	/*
	@PutMapping("/edit-product")
	public EntityProductHome editProduct(@RequestBody EntityProductHome theProduct) {
		
		for(EntityProductVariantByUnitProductHome variant : theProduct.getProductVariantsByUnit()) {
			variant.setProduct(theProduct);
			serviceProduct.updateProductVariantByUnit(variant);
		}
		
		//return serviceProduct.updateEditedProduct(theProduct);	
		return null;
	}
	*/
	
	
	@GetMapping("/search-products/{search}/{itemsPerPage}/{startIndex}")
	public ResponseProductsHome searchProducts(@PathVariable String search,
			@PathVariable Integer startIndex, @PathVariable Integer itemsPerPage) {	
				
		return serviceProduct.searchProduct(search, startIndex, itemsPerPage);			
	}
	
	
	@GetMapping("/get-products-by-category/{categoryId}/{itemsPerPage}/{initialIndex}")
	public ResponseProductsHome getProductsByCategory(@PathVariable Integer categoryId, 
					@PathVariable Integer itemsPerPage, @PathVariable Integer initialIndex) {
		
		return serviceProduct.getProductsByCategory(categoryId, itemsPerPage, initialIndex);	
	}
	
	
	@GetMapping("/get-products-by-brand/{brandId}/{itemsPerPage}/{initialIndex}")
	public ResponseProductsHome getProductsByBrand(@PathVariable Integer brandId,
					@PathVariable Integer itemsPerPage, @PathVariable Integer initialIndex) {
		
		return serviceProduct.getProductsByBrand(brandId, itemsPerPage, initialIndex);
	}
	
	/*
	//PUT- add product-category mappings
	@PutMapping("/products-categories-add")
	public void addProductCategoryEntry(@RequestBody List<Integer> categoryIds) {	
		
		Integer productId = categoryIds.get(0);		
		categoryIds.remove(0);
		
		serviceProduct.addProductCategoryEntry(productId, categoryIds);	
	}
	
	//PUT- delete a product-category mapping

	@PutMapping("/products-categories-delete/{productId}/{categoryId}")
	public void deleteProductCategoryEntry(@PathVariable Integer productId, @PathVariable Integer categoryId) {	
		
		serviceProduct.deleteProductCategoryEntry(productId, categoryId);	
	}
	
	//add mapping for PUT /products - update existing product display
	
	@PutMapping("/products-toggle-display")
	public EntityProductHome updateProductDisplay(@RequestBody EntityProductHome theProduct) {
		
		serviceProduct.updateProductDisplay(theProduct);		
		return theProduct;
	}	
	
	*/


}
