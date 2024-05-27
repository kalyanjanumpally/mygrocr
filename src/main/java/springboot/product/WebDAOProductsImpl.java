package springboot.product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;



@Repository
public class WebDAOProductsImpl implements WebDAOProducts {
	
	@Autowired
	private EntityManager entityManager;

	
	//set up constructor injection
	
	public WebDAOProductsImpl() {	
	}
	
	@Autowired
	public WebDAOProductsImpl(EntityManager theEntityManager) {
		
		this.entityManager = theEntityManager;
	}
	
	@Override
	public List<WebEntityProduct> findAll(Integer itemsPerPage, Integer initialIndex) {
	
		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		//create a query
		Query<WebEntityProduct> theQuery = currentSession.createQuery("from WebEntityProduct");
		
		theQuery.setFirstResult(initialIndex);
		theQuery.setMaxResults(itemsPerPage); 
				
		//execute the query and get result list
		List<WebEntityProduct> dbProducts = theQuery.getResultList();	
		
		List<WebEntityProduct> filteredDbProducts = new ArrayList<>();
		
		for (WebEntityProduct product : dbProducts) {
			
		    boolean variantDisplayON = false;
		    for (WebEntityProductVariantByUnit variant : product.getProductVariantsByUnit()) {
		        if (variant.isVariantDisplay()) {
		            variantDisplayON = true;
		            break; // No need to check further if one variant is displayed
		        }
		    }
		    if (variantDisplayON) {
		    	filteredDbProducts.add(product); // Add the product to the filtered list
		    }
		}
		

		
		//return result	
		return filteredDbProducts;
	}
	
	@Override
	public List<WebEntityProduct> getProductsByCategory(Integer categoryId, Integer itemsPerPage, Integer initialIndex ) {
		
		Query theQuery = (Query) entityManager.createQuery("select distinct p from WebEntityProduct p join p.categories c on c.categoryId = :categoryId ORDER BY -p.sortOrder DESC ");
		theQuery.setParameter("categoryId", categoryId); 

		theQuery.setFirstResult(initialIndex);
		theQuery.setMaxResults(itemsPerPage); 		

		List<WebEntityProduct> dbProducts = theQuery.getResultList();
		
		List<WebEntityProduct> filteredDbProducts = new ArrayList<>();
		
		for (WebEntityProduct product : dbProducts) {
			
		    boolean variantDisplayON = false;
		    for (WebEntityProductVariantByUnit variant : product.getProductVariantsByUnit()) {
		        if (variant.isVariantDisplay()) {
		            variantDisplayON = true;
		            break; // No need to check further if one variant is displayed
		        }
		    }
		    if (variantDisplayON) {
		    	filteredDbProducts.add(product); // Add the product to the filtered list
		    }
		}
		
		return filteredDbProducts;
	}
	
	
	@Override
	public List<WebEntityProduct> getProductsByHomeCategory(Integer categoryId, Integer itemsPerPage, Integer initialIndex) {

		Query theQuery = (Query) entityManager.createQuery("select distinct p from WebEntityProduct p join p.categories c on c.categoryHomeId = :categoryId ORDER BY -p.sortOrder DESC ");
		theQuery.setParameter("categoryId", categoryId); 

		theQuery.setFirstResult(initialIndex);
		theQuery.setMaxResults(itemsPerPage); 		

		List<WebEntityProduct> dbProducts = theQuery.getResultList();
		
		List<WebEntityProduct> filteredDbProducts = new ArrayList<>();
		
		for (WebEntityProduct product : dbProducts) {
			
		    boolean variantDisplayON = false;
		    for (WebEntityProductVariantByUnit variant : product.getProductVariantsByUnit()) {
		        if (variant.isVariantDisplay()) {
		            variantDisplayON = true;
		            break; // No need to check further if one variant is displayed
		        }
		    }
		    if (variantDisplayON) {
		    	filteredDbProducts.add(product); // Add the product to the filtered list
		    }
		}
		
		return filteredDbProducts;
	}
	
	
	@Override
	public List<WebEntityProduct> findProductsByBrand(Integer brandId, Integer itemsPerPage, Integer initialIndex) {
		
		Query theQuery = (Query) entityManager.createQuery("SELECT p FROM WebEntityProduct p JOIN p.brand b where b.brandId = :brandId AND (p.productDeleteStatus IS NULL OR p.productDeleteStatus=:deleteStatus)"
			                                               	+ " AND display=:display");
		theQuery.setParameter("brandId", brandId); 
		theQuery.setParameter("deleteStatus", false);
		theQuery.setParameter("display",true);
		
		theQuery.setFirstResult(initialIndex);
		theQuery.setMaxResults(itemsPerPage); 
		
		List<WebEntityProduct> dbProducts = theQuery.getResultList();
		
		return dbProducts;
	}
	
	
	@Override
	public List<WebEntityProductVariantByUnit> getCartVariants(WebDTOCartVariantIds variantIds) {
		
		List<WebEntityProductVariantByUnit> variants = new ArrayList<>();
		
		for(Integer variantId : variantIds.getVariantIds()) {
			
			WebEntityProductVariantByUnit dbVariant = entityManager.find(WebEntityProductVariantByUnit.class, variantId);
			variants.add(dbVariant);
		}
		return variants;		
	}
	
	@Override
	public List<WebEntityProduct> findCartProductsByIds(WebDTOCartVariantIds variantIds) {
		
		List<WebEntityProduct> dbProducts = new ArrayList<>();
		
		for(Integer variantId : variantIds.getVariantIds()) {
			
			WebEntityProductVariantByUnit dbVariant = entityManager.find(WebEntityProductVariantByUnit.class, variantId);
			
			WebEntityProduct dbProduct = dbVariant.getProduct();	
			
			if(dbVariant.isVariantDisplay()) {
				dbProducts.add(dbProduct);
			}
		}		
		return dbProducts;
	}
	
	
	
	
	@Override
	public Long countOfAllProducts() {
		
		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
			
		Query countQuery = currentSession.createQuery("select count(p.productId) from EntityProduct p where p.productDeleteStatus IS NULL OR p.productDeleteStatus=:deleteStatus"
		                                         		+ " AND display=:display" );
		countQuery.setParameter("deleteStatus", false);
		countQuery.setParameter("display",true);
		Long countResults = (Long) countQuery.uniqueResult();
	
		return countResults;
	}
	
	@Override
	public Long countOfAllProductsByCategory(Integer categoryId) {
		
		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
			
		Query countQuery = currentSession.createQuery("SELECT DISTINCT COUNT(p.productId) FROM WebEntityProduct p JOIN p.categories c WHERE c.categoryId = :categoryId" );
		countQuery.setParameter("categoryId", categoryId);
	//	countQuery.setParameter("deleteStatus", false);
	//	countQuery.setParameter("display",true);
		
		Long countResults = (Long) countQuery.uniqueResult();
	
		return countResults;
	}
	

	@Override
	public Long countOfAllProductsByBrand(Integer brandId) {
		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
			
		Query countQuery = currentSession.createQuery("SELECT count(p.productId) FROM WebEntityProduct p JOIN p.brand b with b.brandId = :brandId AND (p.productDeleteStatus IS NULL OR p.productDeleteStatus=:deleteStatus)"
			                                         	+ "  AND display=:display" );
		countQuery.setParameter("brandId", brandId);
		countQuery.setParameter("deleteStatus", false);
		countQuery.setParameter("display",true);
		
		Long countResults = (Long) countQuery.uniqueResult();
	
		return countResults;
	}


	@Override
	public WebEntityProduct findById(Integer productId, Integer variantId) {
		
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		WebEntityProduct theProduct = currentSession.get(WebEntityProduct.class, productId);
		
		/*
		if((theProduct.isDisplay() == null || theProduct.getProductDeleteStatus().equals(false)) && theProduct.isDisplay()) {
			return theProduct;
		}
		*/
		
		Boolean productActive = false;
		
		for(WebEntityProductVariantByUnit variant : theProduct.getProductVariantsByUnit()) {
			if(variant.isVariantDisplay()) {
				productActive = true;
			}
		}
		
		if(productActive == false) {
			return null;
		}
		else {
			return theProduct;
		}
	}




}
