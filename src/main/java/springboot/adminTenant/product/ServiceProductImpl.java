package springboot.adminTenant.product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ServiceProductImpl implements ServiceProduct {

	
	private DAOProducts dAOProducts;
	private ServiceSearch searchService;
    private DAOBatchesProduct dAOBatches;
	
	@Autowired
	public ServiceProductImpl(DAOProducts theProductsDAO, ServiceSearch theSearchService, DAOBatchesProduct theDAOBatches) {
		
		dAOProducts = theProductsDAO;
		searchService = theSearchService;
		dAOBatches = theDAOBatches;
	}
	
	@Override
	@Transactional
	public ResponseProductsWithBatches findAllProductsAndBatches(Integer itemsPerPage, Integer initialIndex) {

		ResponseProductsWithBatches responseProducts = new ResponseProductsWithBatches();
		
		List<EntityProduct> dbProducts = dAOProducts.findAll(itemsPerPage, initialIndex);	
		List<DTOProductsAndBatches> dtos = dAOBatches.findBatches(dbProducts);
		
		if(initialIndex == 0) {
			Long countResults = dAOProducts.countOfAllProducts();
			responseProducts.setCountOfProducts(countResults);
		}	
		responseProducts.setDtos(dtos);
		
		return responseProducts;
	}
	
	@Override
	@Transactional
	public ResponseProducts getProducts(Integer itemsPerPage, Integer initialIndex) {

		ResponseProducts responseProducts = new ResponseProducts();
		
		List<EntityProduct> dbProducts = dAOProducts.findAll(itemsPerPage, initialIndex);	
		
//		List<DTOProductsAndBatches> dTOs = new ArrayList<DTOProductsAndBatches>();
		
		responseProducts.setDtos(dbProducts);
		
		if(initialIndex == 0) {
			Long countResults = dAOProducts.countOfAllProducts();
			responseProducts.setCountOfProducts(countResults);
		}	
		
		return responseProducts;
	}
	
	@Override
	public ResponseProducts getProductsByCategory(Integer categoryId, Integer itemsPerPage, Integer initialIndex) {
		ResponseProducts responseProducts = new ResponseProducts();
		
		List<EntityProduct> dbProducts = dAOProducts.findProductsByCategory(categoryId, itemsPerPage, initialIndex);	
		
//		List<DTOProductsAndBatches> dTOs = new ArrayList<DTOProductsAndBatches>();
		
		responseProducts.setDtos(dbProducts);
		
		if(initialIndex == 0) {
			Long countResults = dAOProducts.countOfAllProductsByCategory(categoryId);
			responseProducts.setCountOfProducts(countResults);
		}	
		
		return responseProducts;
	}
	
	@Override
	public ResponseProducts getProductsByBrand(Integer brandId, Integer itemsPerPage, Integer initialIndex) {
		ResponseProducts responseProducts = new ResponseProducts();
		
		List<EntityProduct> dbProducts = dAOProducts.findProductsByBrand(brandId, itemsPerPage, initialIndex);	
		
//		List<DTOProductsAndBatches> dTOs = new ArrayList<DTOProductsAndBatches>();
		
		responseProducts.setDtos(dbProducts);
		
		if(initialIndex == 0) {
			Long countResults = dAOProducts.countOfAllProductsByBrand(brandId);
			responseProducts.setCountOfProducts(countResults);
		}	
		
		return responseProducts;
	}
	

	@Override
	@Transactional
	public ResponseProducts findUnlistedProducts(Integer startIndex, Integer itemsPerPage) throws Exception {

		return dAOProducts.findUnlistedProducts(startIndex, itemsPerPage);
	}
	
	@Override
	@Transactional
	public ResponseProducts findUnlistedProductsFromBulkList(Integer startIndex, Integer itemsPerPage) throws Exception {

		return dAOProducts.findUnlistedProductsFromBulkList(startIndex, itemsPerPage);
	}
	
	@Override
	@Transactional
	public ResponseProducts findUnlistedProductsInBrand(Integer brandId, Integer startIndex, Integer itemsPerPage) throws Exception {
		
		return dAOProducts.findUnlistedProductsInBrand(brandId, startIndex, itemsPerPage);
	}
	
	@Override
	@Transactional
	public ResponseProducts searchUnlistedProductsByName(String searchTerm, Integer startIndex, Integer itemsPerPage) throws Exception {
		
		return dAOProducts.searchUnlistedProductsByName(searchTerm, startIndex, itemsPerPage);
	}
	


	
	@Override
	@Transactional
	public ResponseProductsWithBatches findProductsAndBatchesByCategory(Integer categoryId, Integer itemsPerPage, Integer initialIndex) {
		
		ResponseProductsWithBatches responseProducts = new ResponseProductsWithBatches();
		
		List<EntityProduct> dbProducts = dAOProducts.findProductsByCategory(categoryId, itemsPerPage, initialIndex);	
		List<DTOProductsAndBatches> dtos = dAOBatches.findBatches(dbProducts);
		
		if(initialIndex == 0) {
			Long countResults = dAOProducts.countOfAllProductsByCategory(categoryId);
			responseProducts.setCountOfProducts(countResults);
		}	
		responseProducts.setDtos(dtos);
		
		return responseProducts;
	}
	
	/*
	@Override
	@Transactional
	public ResponseProductsWithBatches findProductsAndBatchesByHomeCategory(Integer categoryId, Integer itemsPerPage, Integer initialIndex) {
		ResponseProductsWithBatches responseProducts = new ResponseProductsWithBatches();
		
		List<EntityProduct> dbProducts = dAOProducts.findProductsByHomeCategory(categoryId, itemsPerPage, initialIndex);	
		List<DTOProductsAndBatches> dtos = dAOBatches.findBatches(dbProducts);
		
		if(initialIndex == 0) {
			Long countResults = dAOProducts.countOfAllProductsByHomeCategory(categoryId);
			responseProducts.setCountOfProducts(countResults);
		}	
		responseProducts.setDtos(dtos);
		
		return responseProducts;
	}
	*/


	@Override
	@Transactional
	public void saveProducts(List<EntityProduct> products, String tenantUrl) throws Exception {
		
		dAOProducts.saveProducts(products, tenantUrl);

		
		//remove added produts from unlisted products list
	//	dAOProducts.removeUnlistedProducts(products);		
	}
	
	
	@Override
	@Transactional
	public void addBulkProducts(String tenantUrl) throws Exception {
		dAOProducts.saveBulkProducts(tenantUrl);
		
	}
	
	@Override
	@Transactional
	public void updateVariantDisplay(EntityProductVariantByUnitProduct variant) {

		dAOProducts.updateVariantDisplay(variant);
	}

	@Override
	@Transactional
	public void updateDisplayOutOfStockVariant(EntityProductVariantByUnitProduct variant) {

		dAOProducts.updateDisplayOutOfStockVariant(variant);
	}






	
	/*
	@Override
	@Transactional
	public ResponseProductsWithBatches findProductsAndBatchesByBrand(Integer brandId, Integer itemsPerPage, Integer initialIndex) {		
		
		ResponseProductsWithBatches responseProducts = new ResponseProductsWithBatches();
		
		List<EntityProduct> dbProducts = dAOProducts.findProductsByBrand(brandId, itemsPerPage, initialIndex);	
		List<DTOProductsAndBatches> dtos = dAOBatches.findBatches(dbProducts);
		
		if(initialIndex == 0) {
			Long countResults = dAOProducts.countOfAllProductsByBrand(brandId);
			responseProducts.setCountOfProducts(countResults);
		}	
		responseProducts.setDtos(dtos);
		
		return responseProducts;
	}
	*/

/*
	@Override
	@Transactional
	public EntityProduct save(EntityProduct theProduct) {
		
		return dAOProducts.save(theProduct);

	}
*/
/*
	@Override
	@Transactional
	public DTOProductsAndBatches findProductAndBatchesById(Integer productId) {
		
		EntityProduct dbProduct = dAOProducts.findById(productId);
		List<DTOBatchData> dbBatches = dAOBatches.findBatchesfromProductId(productId);
		
		DTOProductsAndBatches dTO = new DTOProductsAndBatches();
		
		dTO.setBatchesData(dbBatches);
		dTO.setProduct(dbProduct);
		
		return dTO;
	}
*/
/*	
	@Override
	@Transactional
	public EntityProduct findById(Integer theId) {
		
		return dAOProducts.findById(theId);
	}
*/

}
