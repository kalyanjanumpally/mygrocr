package springboot.product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import springboot.product.*;

@Service
public class WebServiceProduct {

	
	private WebDAOProducts webDAOProducts;
	private WebServiceSearch searchService;
    private WebDAOBatchesProduct dAOBatches;
	
	@Autowired
	public WebServiceProduct(WebDAOProducts theProductsDAO, WebServiceSearch theSearchService, WebDAOBatchesProduct theDAOBatches) {
		
		webDAOProducts = theProductsDAO;
		searchService = theSearchService;
		dAOBatches = theDAOBatches;
	}
	
	
	@Transactional
	public WebResponseProducts findAll(Integer itemsPerPage, Integer initialIndex) {

		WebResponseProducts webResponseProducts = new WebResponseProducts();
		
		List<WebEntityProduct> dbProducts = webDAOProducts.findAll(itemsPerPage, initialIndex);	
		List<WebDTOProductAndBatches> dtos = dAOBatches.findBatches(dbProducts);
		
		if(initialIndex == 0) {
			Long countResults = webDAOProducts.countOfAllProducts();
			webResponseProducts.setCountOfProducts(countResults);
		}	
		webResponseProducts.setDtos(dtos);
		
		return webResponseProducts;
	}
	
	
	@Transactional
	public WebDTOProductAndBatches findProductAndBatchesById(Integer productId, Integer variantId) {
		
		WebEntityProduct dbProductVariant = webDAOProducts.findById(productId, variantId);
		List<WebEntityBatchesProduct> dbBatches = dAOBatches.find20LatestBatchesfromVariantId(variantId);
		
		WebDTOProductAndBatches dTO = new WebDTOProductAndBatches();
		
		dTO.setBatchesData(dbBatches);
		dTO.setProduct(dbProductVariant);
		
		return dTO;
	}
	
	
	@Transactional
	public WebResponseCartVariants findCartVariantsAndBatchesByIds(WebDTOCartVariantIds variantIds) {
		
		WebResponseCartVariants webResponseCartVariants = new WebResponseCartVariants();
		
		List<WebDTOCartVariantAndBatches> dtos = new ArrayList<>();		
		List<WebEntityProductVariantByUnit> variants = webDAOProducts.getCartVariants(variantIds);
		
		for(WebEntityProductVariantByUnit variant : variants) {
			
			WebDTOCartVariantAndBatches dto = new WebDTOCartVariantAndBatches();			
			dto.setVariant(variant);			
			List<WebEntityBatchesProduct> dbBatches = dAOBatches.find20LatestBatchesfromVariantId(variant.getVariantId());			
			dto.setBatchesData(dbBatches);	
			dtos.add(dto);
		}
		
		webResponseCartVariants.setDtos(dtos);		
		return webResponseCartVariants;
	}
	
/*
	
	@Transactional
	public WebEntityProduct findById(Integer theId) {
		
		return webDAOProducts.findById(theId);
	}
	*/
	
	
	@Transactional
	public WebResponseProducts getProductsByCategory(Integer categoryId, Integer itemsPerPage, Integer initialIndex) {
		
		WebResponseProducts webResponseProducts = new WebResponseProducts();
		
		List<WebEntityProduct> dbProducts = webDAOProducts.getProductsByCategory(categoryId, itemsPerPage, initialIndex);	
		List<WebDTOProductAndBatches> dtos = dAOBatches.findBatches(dbProducts);
		
		if(initialIndex == 0) {
			Long countResults = webDAOProducts.countOfAllProductsByCategory(categoryId);
			webResponseProducts.setCountOfProducts(countResults);
		}	
		webResponseProducts.setDtos(dtos);
		
		return webResponseProducts;
	}
	
	@Transactional
	public WebResponseProducts getProductsByHomeCategory(Integer categoryId, Integer itemsPerPage, Integer initialIndex) {

		WebResponseProducts webResponseProducts = new WebResponseProducts();
		
		List<WebEntityProduct> dbProducts = webDAOProducts.getProductsByHomeCategory(categoryId, itemsPerPage, initialIndex);	
		List<WebDTOProductAndBatches> dtos = dAOBatches.findBatches(dbProducts);
		
		if(initialIndex == 0) {
			Long countResults = webDAOProducts.countOfAllProductsByCategory(categoryId);
			webResponseProducts.setCountOfProducts(countResults);
		}	
		webResponseProducts.setDtos(dtos);
		
		return webResponseProducts;
	}
	
	
	@Transactional
	public WebResponseProducts findProductsByBrand(Integer brandId, Integer itemsPerPage, Integer initialIndex) {		
		
		WebResponseProducts webResponseProducts = new WebResponseProducts();
		
		List<WebEntityProduct> dbProducts = webDAOProducts.findProductsByBrand(brandId, itemsPerPage, initialIndex);	
		List<WebDTOProductAndBatches> dtos = dAOBatches.findBatches(dbProducts);
		
		if(initialIndex == 0) {
			Long countResults = webDAOProducts.countOfAllProductsByBrand(brandId);
			webResponseProducts.setCountOfProducts(countResults);
		}	
		webResponseProducts.setDtos(dtos);
		
		return webResponseProducts;
	}







}
