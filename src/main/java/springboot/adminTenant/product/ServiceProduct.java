package springboot.adminTenant.product;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface ServiceProduct {
	
	public ResponseProductsWithBatches findAllProductsAndBatches(Integer itemsPerPage, Integer initialIndex);
	
	public ResponseProducts getProducts(Integer itemsPerPage, Integer initialIndex);
	
	public void updateVariantDisplay(EntityProductVariantByUnitProduct variant);

	public ResponseProductsWithBatches findProductsAndBatchesByCategory(Integer categoryId, Integer itemsPerPage, Integer initialIndex);

	//public ResponseProductsWithBatches findProductsAndBatchesByBrand(Integer brandId, Integer itemsPerPage, Integer initialIndex);

	public ResponseProducts findUnlistedProducts(Integer startIndex, Integer itemsPerPage) throws Exception;

	public void saveProducts(List<EntityProduct> products, String tenantUrl) throws Exception;

	public ResponseProducts findUnlistedProductsInBrand(Integer brandId, Integer startIndex, Integer itemsPerPage) throws Exception;

	public ResponseProducts searchUnlistedProductsByName(String searchTerm, Integer startIndex, Integer itemsPerPage) throws Exception;

	public ResponseProducts getProductsByCategory(Integer itemsPerPage, Integer initialIndex, Integer initialIndex2);

	public ResponseProducts getProductsByBrand(Integer itemsPerPage, Integer initialIndex, Integer initialIndex2);

	public void updateDisplayOutOfStockVariant(EntityProductVariantByUnitProduct variant);

	public ResponseProducts findUnlistedProductsFromBulkList(Integer startIndex, Integer itemsPerPage) throws Exception;

	public void addBulkProducts(String tenantUrl) throws Exception;

//	public ResponseProductsWithBatches findProductsAndBatchesByHomeCategory(Integer categoryId, Integer itemsPerPage,
//			Integer initialIndex);

	
	
//  public EntityProduct findById(Integer theId);	
//	public EntityProduct save(EntityProduct theProduct);
//	public DTOProductsAndBatches findProductAndBatchesById(Integer productId);



}
