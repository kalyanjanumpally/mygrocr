package springboot.admin.homeCompany.products;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import springboot.properties.TenantDbProperties;


@Repository
public class DAOHomeProducts {
	
	private EntityManager entityManager;
	private String tenantDbUrl;
	private String tenantDbUsername;
	private String tenantDbPassword;

	
	//set up constructor injection
	
	public DAOHomeProducts() {	
	}
	
	@Autowired
	public DAOHomeProducts(EntityManager theEntityManager, TenantDbProperties tenantDbProperties) {
		
		this.entityManager = theEntityManager;
		this.tenantDbUrl = tenantDbProperties.getTenantsDbUrl();
		this.tenantDbUsername = tenantDbProperties.getTenantDbUsername();
		this.tenantDbPassword = tenantDbProperties.getTenantDbPassword();
	}
	
	public EntityProductHome save(EntityProductHome theProduct) throws Exception {
		Session currentSession = entityManager.unwrap(Session.class);	
		
	//	EntityProduct dbProduct = currentSession.get(EntityProduct.class, theProduct.getProductId());
		/*
		Boolean checkDuplicateSKU = false;
		
		for(EntityProductVariantByUnitProductHome variant : theProduct.getProductVariantsByUnit()) {
		
			Query theQuery = currentSession.createQuery("from EntityProductVariantByUnitHome where sku=:sku");
			theQuery.setParameter("sku",variant.getSku());
			
			List<EntityProductVariantByUnitProductHome> variantsWithSKU = theQuery.getResultList();
		
			if(variantsWithSKU.size() > 0) {
				checkDuplicateSKU = true;
			}
		}
		*/
		
		Boolean checkDuplicateName = false;
		
		Query theQuery = currentSession.createQuery("from EntityProductHome where productName=:productName");
		theQuery.setParameter("productName", theProduct.getProductName());
		
		List<EntityProductHome> duplicateProductName =  theQuery.getResultList();
	
		if(duplicateProductName.size() > 0) {
			checkDuplicateName = true;
		}
	
		if(checkDuplicateName == false) {
			
			List<EntityProductVariantByUnitProductHome> variants = theProduct.getProductVariantsByUnit();
			
			theProduct.setProductVariantsByUnit(null);
			
			EntityProductHome dbProduct = entityManager.merge(theProduct);
			entityManager.flush();
			
			for(EntityProductVariantByUnitProductHome variant : variants) {			
				variant.setProduct(dbProduct);
				entityManager.merge(variant);
				entityManager.flush();
			}	
			
	//		EntityProductHome dbProduct = entityManager.merge(theProduct);
	//		entityManager.flush();

			Query theQueryTenants = currentSession.createQuery("from EntityTenantProductHome");
			
			List<EntityTenantProductHome> dbTenants = theQueryTenants.getResultList();
			
			for(EntityTenantProductHome tenant : dbTenants) {
				
	        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/" + tenant.getTenantUrl() +  "?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
	    		String username = tenantDbUsername;
	    		String password = tenantDbPassword;
			
		        try(
			    		Connection connection = DriverManager.getConnection(url, username, password);			    		
			            Statement statement = connection.createStatement();		
		        ) {	            
			        String addNewUnlistedProductInTenantDb = "INSERT INTO unlisted_products(unlisted_product_home_id, bulk_list, unlisted_product_name, unlisted_product_brand_id) VALUES (" 
			        + dbProduct.getProductId() 
			        + ", " + dbProduct.getBulkList() 
			        + ", \" " + dbProduct.getProductName() 
			        + " \", " + dbProduct.getBrand().getBrandId() + ");"; 	            

		            statement.executeUpdate(addNewUnlistedProductInTenantDb);
		       }
		       catch(Exception e) {
		    	  // e.printStackTrace();
		    	   throw e;
		       }
			}			
			return dbProduct;
		}
		
		return null;		
	}
	
	
	public ResponseProductsHome findAll(Integer itemsPerPage, Integer initialIndex) {
		
		ResponseProductsHome responseProducts = new ResponseProductsHome();
	
		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		//create a query
		Query<EntityProductHome> theQuery = currentSession.createQuery("from EntityProductHome where productDeleteStatus is null OR productDeleteStatus=:productDeleteStatus ORDER BY productId");
		theQuery.setParameter("productDeleteStatus",false);
		
		theQuery.setFirstResult(initialIndex);
		theQuery.setMaxResults(itemsPerPage); 
				
		//execute the query and get result list
		List<EntityProductHome> products = theQuery.getResultList();
		
		if(initialIndex == 0) {
			Query countQuery = currentSession.createQuery("select count(p.productId) from EntityProductHome p where (p.productDeleteStatus IS NULL OR p.productDeleteStatus=:deleteStatus)");
			countQuery.setParameter("deleteStatus", false);
			Long countResults = (Long) countQuery.uniqueResult();
			
			responseProducts.setCountOfProducts(countResults);
		}
		
		responseProducts.setProducts(products);	
		
		//return result	
		return responseProducts;
	}
	
	public DTOProduct findById(Integer productId) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		DTOProduct dTOProduct = new DTOProduct();
		
		EntityProductHome theProduct = currentSession.get(EntityProductHome.class, productId);
		
		if((theProduct.getProductDeleteStatus()== null || theProduct.getProductDeleteStatus().equals(false)) ) {
			
			dTOProduct.setProduct(theProduct);
			
			Query countQuery = (Query) entityManager.createQuery("select size(p.tenants) from EntityProductHome p where p.productId =: productId");
			countQuery.setParameter("productId", productId);
			
			Integer countResults = (Integer) countQuery.uniqueResult();
			
			dTOProduct.setCountOfTenants(countResults);
			
			return dTOProduct;
		}
		else {
			return null;
		}				
	}
	
	
	public void productActiveStatusToggle(Integer productId) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		EntityProductHome dbProduct = currentSession.get(EntityProductHome.class, productId);		
		dbProduct.setProductActiveStatus(!dbProduct.getProductActiveStatus());
		
		entityManager.merge(dbProduct);		

	}
	
	/*
	public void updateProductVariantByUnit(EntityProductVariantByUnitProductHome variant) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		entityManager.merge(variant);
		entityManager.flush();		
	}
	*/
		
	
	public ResponseProductsHome getProductsByCategory(Integer categoryId, Integer itemsPerPage, Integer initialIndex ) {
		
		ResponseProductsHome responseProducts = new ResponseProductsHome();
		
		Query theQuery = (Query) entityManager.createQuery("select distinct p from EntityProductHome p join p.categories c where c.categoryId = :categoryId AND (p.productDeleteStatus IS NULL "
															+ "	OR p.productDeleteStatus=:deleteStatus) order by p.sortOrder");
		theQuery.setParameter("categoryId", categoryId); 
		theQuery.setParameter("deleteStatus", false);
		
		theQuery.setFirstResult(initialIndex);
		theQuery.setMaxResults(itemsPerPage); 
		
		List<EntityProductHome> dbProducts = theQuery.getResultList();
		
		responseProducts.setProducts(dbProducts);
		
		if(initialIndex == 0) {
			
			Query countQuery = (Query) entityManager.createQuery("SELECT DISTINCT COUNT(p.productId) FROM EntityProductHome p JOIN p.categories c WHERE c.categoryId = :categoryId AND (p.productDeleteStatus IS NULL OR p.productDeleteStatus=:deleteStatus)");
			countQuery.setParameter("categoryId", categoryId);
			countQuery.setParameter("deleteStatus", false);
			
			Long countResults = (Long) countQuery.uniqueResult();
			responseProducts.setCountOfProducts(countResults);
		}	
		
		return responseProducts;
	}
	
	
	public ResponseProductsHome getProductsByBrand(Integer brandId, Integer itemsPerPage, Integer initialIndex ) {
		
		ResponseProductsHome responseProducts = new ResponseProductsHome();
		
		Query theQuery = (Query) entityManager.createQuery("SELECT p FROM EntityProductHome p JOIN p.brand b where b.brandId = :brandId AND (p.productDeleteStatus IS NULL OR p.productDeleteStatus=:deleteStatus)  ORDER BY p.productId");
		theQuery.setParameter("brandId", brandId); 
		theQuery.setParameter("deleteStatus", false);
		
		theQuery.setFirstResult(initialIndex);
		theQuery.setMaxResults(itemsPerPage); 
		
		List<EntityProductHome> dbProducts = theQuery.getResultList();
		
		responseProducts.setProducts(dbProducts);
		
		if(initialIndex == 0) {
			
			Query countQuery = (Query) entityManager.createQuery("SELECT count(p.productId) FROM EntityProductHome p JOIN p.brand b with b.brandId = :brandId AND (p.productDeleteStatus IS NULL OR p.productDeleteStatus=:deleteStatus)");
			countQuery.setParameter("brandId", brandId);
			countQuery.setParameter("deleteStatus", false);
			
			Long countResults = (Long) countQuery.uniqueResult();

			responseProducts.setCountOfProducts(countResults);
		}	
		
		return responseProducts;
	}
	
	/*
	public void addProductCategoryEntry(Integer productId, List<Integer> categoryIds) {
		
		Session currentSession = entityManager.unwrap(Session.class);		
		
		EntityProductHome dbProduct = currentSession.get(EntityProductHome.class, productId);
		
		Query theQuery = currentSession.createQuery("from EntityCategoryProductHome", EntityCategoryProductHome.class);
		List<EntityCategoryProductHome> dbCategories = theQuery.getResultList();
		
		List<EntityCategoryProductHome> dbCategoriesInProduct = dbProduct.getCategories();
		
		for (Integer catId : categoryIds) {			
			for(EntityCategoryProductHome cat : dbCategories) {				
				if(catId == cat.getCategoryId()) {
					dbCategoriesInProduct.add(cat);
				}	
			}
		}
        dbProduct.setCategories(dbCategoriesInProduct);	
        currentSession.flush();
	}
	
	
	public void deleteProductCategoryEntry(Integer productId, Integer categoryId) {
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		EntityProductHome dbProduct = currentSession.get(EntityProductHome.class, productId); 
		EntityCategoryProductHome dbCategory = currentSession.get(EntityCategoryProductHome.class, categoryId);
				
		List<EntityCategoryProductHome> categories = dbProduct.getCategories();	
		// Creating iterator object
        Iterator itrCat = categories.iterator();
        // Holds true till there is single element remaining in the object
        while (itrCat.hasNext()) {        	
        	EntityCategoryProductHome iterCategory = (EntityCategoryProductHome) itrCat.next();
            if (iterCategory.getCategoryId() == categoryId) {
                itrCat.remove();
            }
        }
        dbProduct.setCategories(categories);
        currentSession.flush();
        
        List<EntityProductHome> products = dbCategory.getProducts();
		// Creating iterator object
        Iterator itrProd = products.iterator();
  
        // Holds true till there is single element remaining in the object
        while (itrProd.hasNext()) {        	
        	EntityProductHome iterProduct = (EntityProductHome) itrProd.next();
            if (iterProduct.getProductId() == productId) {
                itrProd.remove();
            }
        }
        dbCategory.setProducts(products);
		
		currentSession.saveOrUpdate(dbProduct);	
		currentSession.saveOrUpdate(dbCategory);	
	} 
	*/
	
	/*
	public void updateProductDisplay(EntityProductHome theProduct) {
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		EntityProductHome dbProduct = currentSession.get(EntityProductHome.class, theProduct.getProductId()); 
				
		dbProduct.setDisplay(theProduct.isDisplay());			
		currentSession.saveOrUpdate(dbProduct);		
	}
	*/
	/*
	public EntityProductHome updateEditedProduct(EntityProductHome theProduct) {
		
		Session currentSession = entityManager.unwrap(Session.class);	
		
		List<EntityProductVariantByUnitHome> variants = theProduct.getProductVariantsByUnit();
		
		for(EntityProductVariantByUnitHome variant : variants) {
			variant.setProduct(theProduct);
			entityManager.merge(variant);
			entityManager.flush();
		}
		entityManager.flush();
		entityManager.refresh(theProduct);
	//	EntityProduct dbProduct = currentSession.get(EntityProduct.class,theProduct.getProductId());		
	//	EntityProductHome dbProduct = entityManager.merge(theProduct);	
	
	//	return dbProduct;	
	//	EntityProductHome dbProduct = entityManager.find(EntityProductHome.class, theProduct.getProductId());
		
		return theProduct;
	
		return null;

	}
	*/

}
