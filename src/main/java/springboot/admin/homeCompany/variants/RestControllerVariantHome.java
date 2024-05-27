package springboot.admin.homeCompany.variants;

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
public class RestControllerVariantHome {
	
	private ServiceVariantHome serviceVariantHome;
	
	@Autowired
	public RestControllerVariantHome(ServiceVariantHome theServiceHomeCompanyProduct) {
		serviceVariantHome = theServiceHomeCompanyProduct;		
	}
	
	//add mapping for POST /products - add new product
	
	@PutMapping("/update-variants-by-unit")
	public void addVariants(@RequestBody EntityProductVariantHome theProduct) throws Exception {
		
		List<EntityProductVariantByUnitHome> variants = theProduct.getProductVariantsByUnit();
		theProduct.setProductVariantsByUnit(null);
		
		Boolean bulkListChangeBool = false;
		
		for(EntityProductVariantByUnitHome variant : variants) {
			variant.setProduct(theProduct);
			bulkListChangeBool = bulkListChangeBool || serviceVariantHome.updateProductVariantByUnit(variant);
		}
		
		//now update the edited product and variants in all tenants where this product is already added
		
		serviceVariantHome.updateProductInTenants(theProduct, bulkListChangeBool);
	}
	
	
	@PutMapping("/product-variant-toggle-status/{variantId}")
	public void productVariantToggleStatus(@PathVariable Integer variantId) {
		
		serviceVariantHome.productVariantToggleStatus(variantId);
	}
	
	/*
	@GetMapping("/get-products/{itemsPerPage}/{initialIndex}")
	public ResponseProductsHome findAllProductsAndBatches(@PathVariable Integer itemsPerPage, @PathVariable Integer initialIndex) {
		
		ResponseProductsHome responseProducts = serviceVariantHome.findAll(itemsPerPage, initialIndex);
		
		return responseProducts;
	}
	
	@PutMapping("/delete-product")
	public Boolean deleteProduct(@RequestBody Integer productId) {
		
		EntityProductVariantHome theProduct = serviceVariantHome.findById(productId);
		if(theProduct == null) {
			throw new RuntimeException("Product id not found: " + productId);
		}
		return serviceVariantHome.deleteById(productId);			
	}
	
	
	@GetMapping("/get-product/{productId}")
	public EntityProductVariantHome getProduct(@PathVariable Integer productId) {
					
		EntityProductVariantHome theProduct = serviceVariantHome.findById(productId);		
		if(theProduct == null) {			
			return null;
		} 
		else {
			return theProduct;
		}		
	}
	
	
	
	@PutMapping("/edit-product")
	public EntityProductVariantHome editProduct(@RequestBody EntityProductVariantHome theProduct) {
		
		System.out.println("Reaching edit product");
		System.out.println("no of variants: " + theProduct.getProductVariantsByUnit().size());
		
		for(EntityProductVariantByUnitHome variant : theProduct.getProductVariantsByUnit()) {
			variant.setProduct(theProduct);
			serviceVariantHome.updateProductVariantByUnit(variant);
		}
		
		//return serviceVariantHome.updateEditedProduct(theProduct);	
		return null;
	}
	
	
	//add mapping for PUT /products - update existing product display
	
	@PutMapping("/products-toggle-display")
	public EntityProductVariantHome updateProductDisplay(@RequestBody EntityProductVariantHome theProduct) {
		
		serviceVariantHome.updateProductDisplay(theProduct);		
		return theProduct;
	}
	
	@GetMapping("/search-products/{search}/{itemsPerPage}/{startIndex}")
	public ResponseProductsHome searchProducts(@PathVariable String search,
			@PathVariable Integer startIndex, @PathVariable Integer itemsPerPage) {	
				
		return serviceVariantHome.searchProduct(search, startIndex, itemsPerPage);			
	}
	
	
	@GetMapping("/search-products-by-category/{categoryId}/{itemsPerPage}/{initialIndex}")
	public ResponseProductsHome searchProductsByCategory(@PathVariable Integer categoryId, 
					@PathVariable Integer itemsPerPage, @PathVariable Integer initialIndex) {
		
		return serviceVariantHome.findProductsByCategory(categoryId, itemsPerPage, initialIndex);	
	}
	
	
	@GetMapping("/search-products-by-brand/{brandId}/{itemsPerPage}/{initialIndex}")
	public ResponseProductsHome searchProductsByBrand(@PathVariable Integer brandId,
					@PathVariable Integer itemsPerPage, @PathVariable Integer initialIndex) {
		
		return serviceVariantHome.findProductsByBrand(brandId, itemsPerPage, initialIndex);
	}
	
	
	//PUT- add product-category mappings
	@PutMapping("/products-categories-add")
	public void addProductCategoryEntry(@RequestBody List<Integer> categoryIds) {	
		
		Integer productId = categoryIds.get(0);		
		categoryIds.remove(0);
		
		serviceVariantHome.addProductCategoryEntry(productId, categoryIds);	
	}
	
	//PUT- delete a product-category mapping

	@PutMapping("/products-categories-delete/{productId}/{categoryId}")
	public void deleteProductCategoryEntry(@PathVariable Integer productId, @PathVariable Integer categoryId) {	
		
		serviceVariantHome.deleteProductCategoryEntry(productId, categoryId);	
	}
	
	*/


}
