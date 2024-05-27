package springboot.adminTenant.product;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import springboot.adminTenant.purchase.DtoPurchase;
import springboot.adminTenant.purchase.EntityBatchesPurchase;
import springboot.adminTenant.purchase.EntityPurchaseInvoice;
import springboot.adminTenant.purchase.EntitySupplierPurchase;
import springboot.adminTenant.purchase.ServicePurchase;
import springboot.properties.TenantDbProperties;


@Repository
public class DAOProducts{

	private EntityManager entityManager;
	private ServicePurchase servicePurchase;
	
	private String tenantDbUrl;
	private String tenantDbUsername;
	private String tenantDbPassword;
	
	//private final WebClient webClient;
	
	//set up constructor injection
	
	public DAOProducts() {	
	}
	
	
	@Autowired
	public DAOProducts(EntityManager theEntityManager, ServicePurchase servicePurchase, TenantDbProperties tenantDbProperties) {

		this.entityManager = theEntityManager;
		this.servicePurchase = servicePurchase;
		this.tenantDbUrl = tenantDbProperties.getTenantsDbUrl();
		this.tenantDbUsername = tenantDbProperties.getTenantDbUsername();
		this.tenantDbPassword = tenantDbProperties.getTenantDbPassword();
	}
	
	
	public List<EntityProduct> findAll(Integer itemsPerPage, Integer initialIndex) {
	
		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		//create a query
		Query<EntityProduct> theQuery = currentSession.createQuery("from EntityProduct");
		
		theQuery.setFirstResult(initialIndex);
		theQuery.setMaxResults(itemsPerPage); 
				
		//execute the query and get result list
		List<EntityProduct> products = theQuery.getResultList();
		
		//return result	
		return products;
	}
	
	
	public ResponseProducts findUnlistedProducts(Integer startIndex, Integer itemsPerPage) throws Exception {
		
		ResponseProducts responseProducts = new ResponseProducts();
	//	List<EntityProduct> dtos = new ArrayList<>();
		
		
		Query theQuery = (Query) entityManager.createQuery("from EntityUnlistedProduct");
		theQuery.setFirstResult(startIndex);
		theQuery.setMaxResults(itemsPerPage); 
		
		List<EntityUnlistedProduct> unlistedProducts = theQuery.getResultList();
		
		Query countQuery = (Query) entityManager.createQuery("select count(p.unlistedProductId) from EntityUnlistedProduct p");
		Long countResults = (Long) countQuery.uniqueResult();
		
		responseProducts.setCountOfProducts(countResults);
       
       List<EntityProduct> dtos = getProductsFromUnlistedProducts(unlistedProducts);
		
       responseProducts.setDtos(dtos);
		//return products;
       return responseProducts;
	}
	
	

	public ResponseProducts findUnlistedProductsFromBulkList(Integer startIndex, Integer itemsPerPage) throws Exception {

		ResponseProducts responseProducts = new ResponseProducts();
	//	List<EntityProduct> dtos = new ArrayList<>();
		
		
		Query theQuery = (Query) entityManager.createQuery("from EntityUnlistedProduct where bulkList=:bulkList");
		theQuery.setParameter("bulkList", true);
		theQuery.setFirstResult(startIndex);
		theQuery.setMaxResults(itemsPerPage); 
		
		List<EntityUnlistedProduct> unlistedProducts = theQuery.getResultList();
		
		Query countQuery = (Query) entityManager.createQuery("select count(unlistedProductId) from EntityUnlistedProduct where bulkList=:bulkList");
		countQuery.setParameter("bulkList", true);
		Long countResults = (Long) countQuery.uniqueResult();
		
		responseProducts.setCountOfProducts(countResults);
       
       List<EntityProduct> dtos = getProductsFromUnlistedProducts(unlistedProducts);
		
       responseProducts.setDtos(dtos);
		//return products;
       return responseProducts;
	}
	
	
	
	public ResponseProducts findUnlistedProductsInBrand(Integer brandId, Integer startIndex, Integer itemsPerPage) throws Exception {
		
		ResponseProducts responseProducts = new ResponseProducts();
		
		
		Query theQuery = (Query) entityManager.createQuery("from EntityUnlistedProduct where unlistedProductBrandId=:brandId");
		theQuery.setParameter("brandId", brandId);
		theQuery.setFirstResult(startIndex);
		theQuery.setMaxResults(itemsPerPage); 
		
		List<EntityUnlistedProduct> unlistedProducts = theQuery.getResultList();
		
		
		Query countQuery = (Query) entityManager.createQuery("select count(p.unlistedProductId) from EntityUnlistedProduct p where unlistedProductBrandId=:brandId");
		countQuery.setParameter("brandId", brandId);
		Long countResults = (Long) countQuery.uniqueResult();
		
		responseProducts.setCountOfProducts(countResults);
		
		List<EntityProduct> dtos = getProductsFromUnlistedProducts(unlistedProducts);
		
		responseProducts.setDtos(dtos);

       return responseProducts;
	}
	
	
	
	public ResponseProducts searchUnlistedProductsByName(String searchTerm, Integer startIndex, Integer itemsPerPage) throws Exception {
		
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		
        try {
			fullTextEntityManager.createIndexer().startAndWait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(EntityUnlistedProduct.class).get();
        org.apache.lucene.search.Query luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1).onFields("unlistedProductName")
                .matching(searchTerm).createQuery();

        javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, EntityUnlistedProduct.class);

        // execute search
        
		jpaQuery.setFirstResult(startIndex);
		jpaQuery.setMaxResults(itemsPerPage);

        List<EntityUnlistedProduct> unlistedProducts = null;
        try {
            unlistedProducts = jpaQuery.getResultList();
        } catch (NoResultException nre) {
        	nre.printStackTrace();
        }
        
		ResponseProducts responseProducts = new ResponseProducts();
        
		if(startIndex == 0) {
			Integer countResults = ((FullTextQuery) jpaQuery).getResultSize();
			if(countResults != null) {
				responseProducts.setCountOfProducts( Long.valueOf(countResults));
			}
		}

		List<EntityProduct> dtos = getProductsFromUnlistedProducts(unlistedProducts);
		
       responseProducts.setDtos(dtos);

       return responseProducts;
	}
	
	
	
	
	public List<EntityProduct> getProductsFromUnlistedProducts(List<EntityUnlistedProduct> unlistedProducts) throws Exception {
		
			List<EntityProduct> dtos = new ArrayList<>();
		
		
	       try {       	
	        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/home_company?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
	    		String username = tenantDbUsername;
	    		String password = tenantDbPassword;
	    		Connection connection = DriverManager.getConnection(url, username, password);
	    		
	    	    //		Connection connection = dataSource.getConnection();
	            Statement statement = connection.createStatement();
	    		
	    		for(EntityUnlistedProduct unlistedProduct : unlistedProducts) {
	    			
	    			EntityProduct product = new EntityProduct();
	    		//	EntityProduct dto = new EntityProduct();
	    			List<EntityCategoryProduct> categories = new ArrayList<>();
	    			EntityBrandProduct brand = new EntityBrandProduct();
	    			
			        String getUnlistedProductsData = "SELECT * FROM  products  WHERE product_id = "
			        							+ unlistedProduct.getUnlistedProductHomeId() 
			        							+ " AND (product_delete_status is NULL OR product_delete_status = FALSE) AND  (product_active_status is NULL OR product_active_status = TRUE)  LIMIT 1;"; 	            
			        
			        ResultSet resultSet = statement.executeQuery(getUnlistedProductsData);
			        
			        Integer brandId = 0;
		            
			        if (resultSet.next()) {
			    //    	product.setProductId(resultSet.getInt("product_id"));
			        	product.setProductHomeId(resultSet.getInt("product_id"));
			        	product.setProductName(resultSet.getString("product_name"));
			        	product.setDescription(resultSet.getString("description"));
			     //   	product.setQuantity(10F);
			     //   	product.setImagesOrder(resultSet.getString("images_order"));
			     //   	product.setDisplay(resultSet.getBoolean("display"));
			     //   	product.setKgPiecesName(resultSet.getString("kg_pieces_name"));
			     //   	product.setKgPiecesPerUnit(resultSet.getFloat("kg_pieces_per_unit"));
			        	product.setHasExpiryDate(resultSet.getBoolean("has_expiry_date"));
			     //   	product.setSku(resultSet.getString("sku"));
			     //   	product.setProductMrp(resultSet.getFloat("product_mrp"));
			        	product.setDateCreated(resultSet.getDate("date_created"));
			        	product.setLastUpdated(resultSet.getDate("last_updated"));
			        	product.setHsnCode(resultSet.getString("hsn_code"));
			        	product.setGst(resultSet.getInt("gst"));
			        	product.setSortOrder(resultSet.getInt("sort_order"));
			        	product.setMetaTitle(resultSet.getString("meta_title"));
			        	product.setMetaDescription(resultSet.getString("meta_description"));
			        	product.setMetaKeywords(resultSet.getString("meta_keywords"));
			        	product.setSeoURL(resultSet.getString("seo_url"));	
			        	
			        	brandId = resultSet.getInt("product_brand_id");
			        }
			        
			        if(product.getProductHomeId().equals(unlistedProduct.getUnlistedProductHomeId())) {
			        
			        	String getVariantsOfUnlistedProduct = "SELECT * FROM product_variant_by_unit WHERE variant_product_id = " + unlistedProduct.getUnlistedProductHomeId() +
			        									  " AND (variant_active_status is NULL OR variant_active_status = TRUE)";
			        
				        ResultSet resultSetVariants = statement.executeQuery(getVariantsOfUnlistedProduct);
				        
				        List<EntityProductVariantByUnitProduct> variants = new ArrayList<>();
		   
				        while(resultSetVariants.next()) {
				        	EntityProductVariantByUnitProduct variant = new EntityProductVariantByUnitProduct();
				        	
				        	variant.setVariantHomeId(resultSetVariants.getInt("variant_id"));
				       // 	variant.setProduct(product);
				        	variant.setUnit(resultSetVariants.getString("unit"));
				        	variant.setImagesOrder(resultSetVariants.getString("images_order"));
				        	variant.setKgPiecesName(resultSetVariants.getString("kg_pieces_name"));
				        	variant.setKgPiecesPerUnit(resultSetVariants.getFloat("kg_pieces_per_unit"));
				        	variant.setSku(resultSetVariants.getString("sku"));
				        	variant.setVariantMrp(resultSetVariants.getFloat("variant_MRP"));
				        	variant.setVariantDisplay(true);
				        	variant.setDisplayOutOfStockVariant(true);
				        	variant.setQuantity(100F);
					        
				        	variants.add(variant);
				        }
				        
				        for(EntityProductVariantByUnitProduct variant: variants) {
				        	
			    			List<EntityImageProduct> images = new ArrayList<>();
				                	
					        String getImagesOfVariant = "SELECT * FROM images i JOIN product_variant_by_unit_image pi ON i.image_id = pi.image_id  WHERE pi.variant_id = " + variant.getVariantHomeId(); 
		
					        ResultSet resultSetImages = statement.executeQuery(getImagesOfVariant);
					        
					        while (resultSetImages.next()) {
					        	EntityImageProduct image = new EntityImageProduct();
					        	image.setImageId(resultSetImages.getInt("image_id"));
					        	image.setImageName(resultSetImages.getString("image_name"));
					        	image.setImageUrl(resultSetImages.getString("image_url"));	
					        	images.add(image);
					        }
				        	variant.setImages(images);		        	
				        }
				        
				        product.setProductVariantsByUnit(variants);
				        
		
				    //    product.setImages(images);
				        
				        String getBrandOfUnlistedProduct = "SELECT * FROM brands WHERE brand_id = " + brandId + " LIMIT 1"; 	            
		
				        ResultSet resultSetBrand = statement.executeQuery(getBrandOfUnlistedProduct);
				        
				        
				        
				        if (resultSetBrand.next()) {
				        	brand.setBrandId(resultSetBrand.getInt("brand_id"));
				        	brand.setBrandName(resultSetBrand.getString("brand_name"));
				        }		        		        
				        
				        product.setBrand(brand);
				        
				        String getCategoriesOfUnlistedProduct = "SELECT * FROM categories c JOIN product_category pc ON c.category_id = pc.category_id  WHERE pc.product_id = " + unlistedProduct.getUnlistedProductHomeId(); 	            
		
				        ResultSet resultSetCategories = statement.executeQuery(getCategoriesOfUnlistedProduct);
				        
				        while (resultSetCategories.next()) {
				        	EntityCategoryProduct category = new EntityCategoryProduct();
				        	category.setCategoryId(resultSetCategories.getInt("category_id"));
				        	category.setParentCategoryId(resultSetCategories.getInt("parent_category_id"));	
				        	category.setCategoryName(resultSetCategories.getString("category_name"));
				        	categories.add(category);
				        }
				        product.setCategories(categories);
				        dtos.add(product);
			        }
	    		}
	       }
	       catch(Exception e) {
	    	//   e.printStackTrace();
	    	   throw e;
	    	   
	       }
	       return dtos;
	}
	

	
	
	public List<EntityProduct> findProductsByCategory(Integer categoryId, Integer itemsPerPage, Integer initialIndex ) {
		
		Query theQuery = (Query) entityManager.createQuery("select distinct p from EntityProduct p join p.categories c where c.categoryId = :categoryId order by p.sortOrder");
		theQuery.setParameter("categoryId", categoryId); 
		
		theQuery.setFirstResult(initialIndex);
		theQuery.setMaxResults(itemsPerPage); 
		
		List<EntityProduct> dbProducts = theQuery.getResultList();
		
		return dbProducts;
	}
	
	/*
	public List<EntityProduct> findProductsByHomeCategory(Integer categoryId, Integer itemsPerPage, Integer initialIndex) {
		
		Query theQuery = (Query) entityManager.createQuery("select distinct p from EntityProduct p join p.categories c where c.categoryHomeId = :categoryId order by p.sortOrder");
		theQuery.setParameter("categoryId", categoryId); 
		
		theQuery.setFirstResult(initialIndex);
		theQuery.setMaxResults(itemsPerPage); 
		
		List<EntityProduct> dbProducts = theQuery.getResultList();
		
		return dbProducts;
	}
	*/
	
	
	public List<EntityProduct> findProductsByBrand(Integer brandId, Integer itemsPerPage, Integer initialIndex) {
		
		Query theQuery = (Query) entityManager.createQuery("SELECT p FROM EntityProduct p JOIN p.brand b where b.brandId = :brandId ORDER BY p.productId");
		theQuery.setParameter("brandId", brandId); 
		
		theQuery.setFirstResult(initialIndex);
		theQuery.setMaxResults(itemsPerPage); 
		
		List<EntityProduct> dbProducts = theQuery.getResultList();
		
		return dbProducts;
	}
	
	
	
	
	
	public Long countOfAllProducts() {
		
		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
			
		Query countQuery = currentSession.createQuery("select count(p.productId) from EntityProduct p");
		Long countResults = (Long) countQuery.uniqueResult();
	
		return countResults;
	}
	
	
	public Long countOfAllProductsByCategory(Integer categoryId) {
		
		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
			
		Query countQuery = currentSession.createQuery("SELECT DISTINCT COUNT(p.productId) FROM EntityProduct p JOIN p.categories c WHERE c.categoryId = :categoryId");
		countQuery.setParameter("categoryId", categoryId);
		
		Long countResults = (Long) countQuery.uniqueResult();
	
		return countResults;
	}
	
	
	public Long countOfAllProductsByHomeCategory(Integer categoryId) {
		
		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
			
		Query countQuery = currentSession.createQuery("SELECT DISTINCT COUNT(p.productId) FROM EntityProduct p JOIN p.categories c WHERE c.categoryHomeId = :categoryId");
		countQuery.setParameter("categoryId", categoryId);
		
		Long countResults = (Long) countQuery.uniqueResult();
	
		return countResults;
	}
	

	
	public Long countOfAllProductsByBrand(Integer brandId) {
		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
			
		Query countQuery = currentSession.createQuery("SELECT count(p.productId) FROM EntityProduct p JOIN p.brand b with b.brandId = :brandId");
		countQuery.setParameter("brandId", brandId);
		
		Long countResults = (Long) countQuery.uniqueResult();
	
		return countResults;
	}


	
	
	public void saveProducts(List<EntityProduct> products, String tenantUrl) throws Exception {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		List<EntityProduct> dbProducts = new ArrayList<>();
		
		Query theQueryCategories = currentSession.createQuery("from EntityCategoryProduct");		
		List<EntityCategoryProduct> dbCategories = theQueryCategories.getResultList();
		
		Query theQueryBrands = currentSession.createQuery("from EntityBrandProduct");		
		List<EntityBrandProduct> dbBrands = theQueryBrands.getResultList();		
		
		
		for(EntityProduct product : products) {
			
			Query theQuery = currentSession.createQuery("from EntityProduct where productHomeId=:productHomeId");
			theQuery.setParameter("productHomeId",product.getProductHomeId());
			
			List<EntityProduct> checkProduct =  theQuery.getResultList();
			
			if(checkProduct.size() == 0) {
				
				product.setProductId(0);
				
				List<Integer> categoryHomeIds = new ArrayList<>();
				
				for(EntityCategoryProduct category : product.getCategories()) {								
					categoryHomeIds.add(category.getCategoryId());	
				}
				
				product.setCategories(null);
				List<EntityCategoryProduct> dbCategoriesOfProduct = new ArrayList<>();
				
				for(Integer categoryHomeId : categoryHomeIds) {					
					for(EntityCategoryProduct dbCategory : dbCategories) {						
						if(dbCategory.getCategoryHomeId().equals(categoryHomeId)) {
							
							dbCategoriesOfProduct.add(dbCategory);							
						}												
					}					
				}
				product.setCategories(dbCategoriesOfProduct);
								
				for(EntityBrandProduct dbBrand : dbBrands) {																
					if(product.getBrand().getBrandId().equals(dbBrand.getBrandHomeId())) {							
						product.setBrand(dbBrand);						
					}																	
				}	
				
				
				Set<EntityImageProduct> dbImages = new HashSet<>();
				
				for(EntityProductVariantByUnitProduct variant : product.getProductVariantsByUnit()) {
					
					variant.setProduct(product);
					
					for(EntityImageProduct image : variant.getImages()) {
												
						Query theQueryImage = currentSession.createQuery("from EntityImageProduct where imageHomeId=:imageHomeId");
						theQueryImage.setParameter("imageHomeId", image.getImageId());
						
						List<EntityImageProduct> searchDbImages =  theQueryImage.getResultList();
						
						if(searchDbImages.size() != 0) {
							image.setImageHomeId(image.getImageId());
							dbImages.add(searchDbImages.get(0));
						}
						else {
							image.setImageHomeId(image.getImageId());
							image.setImageId(0);
							EntityImageProduct newDbImage = entityManager.merge(image);
							entityManager.flush();
							dbImages.add(newDbImage);
						}		
					}				
				}
				
				for(EntityProductVariantByUnitProduct variant : product.getProductVariantsByUnit()) {
					
					variant.setVariantId(0);

					List<Integer> imagesHomeIds = new ArrayList<>(); 
					
					for(EntityImageProduct image : variant.getImages()) {						
						imagesHomeIds.add(image.getImageHomeId());
					}
					
					variant.setImages(null);
					List<EntityImageProduct> dbImagesOfVariant = new ArrayList<>();
					
					for(Integer imageHomeId : imagesHomeIds) {
						
						for(EntityImageProduct dbImage : dbImages) {	
							if(dbImage.getImageHomeId() == imageHomeId) {
								dbImagesOfVariant.add(dbImage);
							}	
						}
					}
					
					variant.setImages(dbImagesOfVariant);					
				//	entityManager.merge(variant);
				//	entityManager.flush();					
				}
				
				
				EntityProduct dbProduct = entityManager.merge(product);
				dbProducts.add(dbProduct);
			}
		}
		
		removeUnlistedProducts(products);
		
		createPurchaseInvoiceForInitialStocks(dbProducts);
		
		//add products to tenant in home_company database
				
       try {       	
        	String url = "jdbc:mysql://" +  tenantDbUrl + ":3306/home_company?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
    		String username = tenantDbUsername;
    		String password = tenantDbPassword;
    		Connection connection = DriverManager.getConnection(url, username, password);
    		
    	    //		Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();

	        String getTenantId = "SELECT tenant_id from tenants WHERE tenant_url = \"" + tenantUrl + "\" LIMIT 1"; 	            
	    	
            ResultSet resultSet = statement.executeQuery(getTenantId);
            
            Integer tenantId = 0;
            while (resultSet.next()) {
            	tenantId = resultSet.getInt("tenant_id");
            }
 
            for(EntityProduct product : dbProducts) {   
            	
            	String addProductTenantEntry =  "INSERT INTO product_tenant (product_id, tenant_id) VALUES (" + product.getProductHomeId() + ", " + tenantId + ")";                      	
            	statement.executeUpdate(addProductTenantEntry); 
            }		
       }
       catch(Exception e) {
    	   throw e;
       }
		
	//	return dbProducts;
	}
	
	
	
	public void saveBulkProducts(String tenantUrl) throws Exception {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query theQueryUnlistedBulkProducts = currentSession.createQuery("from EntityUnlistedProduct where bulkList=:bulkList");		
		theQueryUnlistedBulkProducts.setParameter("bulkList", true);	
		
		List<EntityUnlistedProduct> dbUnlistedProducts = theQueryUnlistedBulkProducts.getResultList();
		
		List<EntityProduct> dbProducts = getProductsFromUnlistedProducts(dbUnlistedProducts);
		
		saveProducts(dbProducts, tenantUrl);	
	}
	
	
	public void createPurchaseInvoiceForInitialStocks(List<EntityProduct> dbProducts) {
		
		Session currentSession = entityManager.unwrap(Session.class);

		Date toDate = new Date(); 
		Long millis = 30L * 24L * 60L * 60L * 1000L;;
		Date fromDate = new Date(toDate.getTime() - millis);
		
		DtoPurchase initialStockPurchase = servicePurchase.findInitialStockPurchaseInvoice(fromDate, toDate);
		
		Long currentTimeMillis = System.currentTimeMillis();
		java.sql.Date todayDate = new java.sql.Date(currentTimeMillis);
		
		java.sql.Date expiryDate = new java.sql.Date(currentTimeMillis + (500L * 365L * 24L * 60L * 60L * 1000L));  //500 years from now
		
        LocalDate localDate = todayDate.toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateString = localDate.format(formatter);
		
		if( initialStockPurchase == null || (initialStockPurchase != null && initialStockPurchase.getBatches().size() > 100) ) { //purchase batches in entry greater than 100, so create a new purchase entry/ no purchase entry
			DtoPurchase dtoPurchase = new DtoPurchase();
			
			EntityPurchaseInvoice purchaseInvoice = new EntityPurchaseInvoice();
			List<EntityBatchesPurchase> dtoBatchAndProductPurchases = new ArrayList<>();
			
			Query theQuery = currentSession.createQuery("from EntitySupplierPurchase where supplierName=:supplierName");
			theQuery.setParameter("supplierName", "Initial Stock");
			
			EntitySupplierPurchase supplier = (EntitySupplierPurchase) theQuery.getSingleResult();
			
			purchaseInvoice.setSupplier(supplier);
			purchaseInvoice.setDateCreated(todayDate);
			purchaseInvoice.setAmount(0F);
			purchaseInvoice.setPurchaseInvoiceId((long) 0);
			purchaseInvoice.setNumberOfBatches(300);
			
	//		EntityPurchaseInvoice dbPurchaseInvoice = entityManager.merge(purchaseInvoice);
			
			dtoPurchase.setPurchaseInvoice(purchaseInvoice);
			
			for(EntityProduct product : dbProducts) {
				
				for(EntityProductVariantByUnitProduct variant : product.getProductVariantsByUnit()) {
					
					EntityBatchesPurchase dtoBatchAndProductPurchase = new EntityBatchesPurchase();
				
					dtoBatchAndProductPurchase.setBatchId((long) 0);
					dtoBatchAndProductPurchase.setBatchNo(dateString);
					dtoBatchAndProductPurchase.setBatchBrandName(product.getBrand().getBrandName());
					dtoBatchAndProductPurchase.setBatchProductId(product.getProductId());
					dtoBatchAndProductPurchase.setBatchVariantId(variant.getVariantId());					
					dtoBatchAndProductPurchase.setBatchProductHomeId(product.getProductHomeId());
					dtoBatchAndProductPurchase.setBatchVariantHomeId(variant.getVariantHomeId());					
					dtoBatchAndProductPurchase.setBatchProductName(product.getProductName());
					dtoBatchAndProductPurchase.setBatchUnit(variant.getUnit());
			//		dtoBatchAndProductPurchase.setBatchProductSku(product.getSku());
			//		dtoBatchAndProductPurchase.setBatchPurchaseInvoiceId(dbPurchaseInvoice.getPurchaseInvoiceId()); this is done later in purchase service.
					dtoBatchAndProductPurchase.setBatchPurchasePrice(0F);
					dtoBatchAndProductPurchase.setBatchPurSaleBool(0);
					dtoBatchAndProductPurchase.setDisplay(true);
					dtoBatchAndProductPurchase.setExpiryDate(expiryDate);
					dtoBatchAndProductPurchase.setMrp(variant.getVariantMrp());
					dtoBatchAndProductPurchase.setPpIncludesGST(false);
					dtoBatchAndProductPurchase.setBatchGST(product.getGst());
					dtoBatchAndProductPurchase.setQuantity(variant.getQuantity());
					dtoBatchAndProductPurchase.setCurrentQuantity(variant.getQuantity());
					dtoBatchAndProductPurchase.setSellingPrice(variant.getVariantMrp());
					dtoBatchAndProductPurchase.setTransactionStatus(null);
					
					dtoBatchAndProductPurchases.add(dtoBatchAndProductPurchase);
				}
			}
			
			dtoPurchase.setBatches(dtoBatchAndProductPurchases);
			
			servicePurchase.saveInitialStockPurchase(dtoPurchase);
		}	
		else {  //batch entries less than 100, so add in existing purchase invoice
			
			//DtoPurchase initialStockPurchase
			List<EntityBatchesPurchase> dtoBatchAndProductPurchases = new ArrayList<>();

			for(EntityProduct product : dbProducts) {

				for(EntityProductVariantByUnitProduct variant : product.getProductVariantsByUnit()) {
				
					EntityBatchesPurchase dtoBatchAndProductPurchase = new EntityBatchesPurchase();
				
					dtoBatchAndProductPurchase.setBatchId((long) 0);
					dtoBatchAndProductPurchase.setBatchNo(dateString);
					dtoBatchAndProductPurchase.setBatchBrandName(product.getBrand().getBrandName());
					dtoBatchAndProductPurchase.setBatchProductId(product.getProductId());
					dtoBatchAndProductPurchase.setBatchVariantId(variant.getVariantId());					
					dtoBatchAndProductPurchase.setBatchProductHomeId(product.getProductHomeId());
					dtoBatchAndProductPurchase.setBatchVariantHomeId(variant.getVariantHomeId());					
					dtoBatchAndProductPurchase.setBatchProductName(product.getProductName());
					dtoBatchAndProductPurchase.setBatchUnit(variant.getUnit());
			//		dtoBatchAndProductPurchase.setBatchProductSku(product.getSku());
					dtoBatchAndProductPurchase.setBatchPurchaseInvoiceId(initialStockPurchase.getPurchaseInvoice().getPurchaseInvoiceId());
					dtoBatchAndProductPurchase.setBatchPurchasePrice(0F);
					dtoBatchAndProductPurchase.setBatchPurSaleBool(0);
					dtoBatchAndProductPurchase.setDisplay(true);
					dtoBatchAndProductPurchase.setExpiryDate(expiryDate);
					dtoBatchAndProductPurchase.setMrp(variant.getVariantMrp());
					dtoBatchAndProductPurchase.setPpIncludesGST(false);
					dtoBatchAndProductPurchase.setBatchGST(product.getGst());
					dtoBatchAndProductPurchase.setQuantity(variant.getQuantity());
					dtoBatchAndProductPurchase.setCurrentQuantity(variant.getQuantity());
					dtoBatchAndProductPurchase.setSellingPrice(variant.getVariantMrp());
					dtoBatchAndProductPurchase.setTransactionStatus(null);
					
					dtoBatchAndProductPurchases.add(dtoBatchAndProductPurchase);
				}
			}
			
			initialStockPurchase.setBatches(dtoBatchAndProductPurchases);
			servicePurchase.updateInitialStockPurchase(initialStockPurchase);					
		}		
	}

	
	
	public void removeUnlistedProducts(List<EntityProduct> products) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		for(EntityProduct product : products) {
		
			Query theQuery = currentSession.createQuery("delete EntityUnlistedProduct where unlistedProductHomeId=:unlistedProductHomeId");
			theQuery.setParameter("unlistedProductHomeId", product.getProductHomeId());
			
			theQuery.executeUpdate();
		}
		
	}
	
	
	
	public void updateVariantDisplay(EntityProductVariantByUnitProduct variant) {
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		EntityProductVariantByUnitProduct dbVariant = currentSession.get(EntityProductVariantByUnitProduct.class, variant.getVariantId()); 
	//	EntityAdminCategory2 dbCategory = currentSession.get(EntityAdminCategory2.class, theProduct.getProductId());
				
		dbVariant.setVariantDisplay(variant.isVariantDisplay());
	//	dbProduct.setCategories(theProduct.getCategories());
				
		currentSession.saveOrUpdate(dbVariant);		
	}


	public void updateDisplayOutOfStockVariant(EntityProductVariantByUnitProduct variant) {
		
		Session currentSession = entityManager.unwrap(Session.class);

		EntityProductVariantByUnitProduct dbVariant = currentSession.get(EntityProductVariantByUnitProduct.class, variant.getVariantId()); 	
		
		dbVariant.setDisplayOutOfStockVariant(variant.isDisplayOutOfStockVariant());	

		currentSession.saveOrUpdate(dbVariant);
		currentSession.flush();
	}











	
	/*
	
	public EntityProduct findById(Integer productId) {
		
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		EntityProduct theProduct = currentSession.get(EntityProduct.class, productId);
				
		return theProduct;

	}
*/	
	


/*	
	public void save(EntityProduct theProduct) {
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);	
		
		EntityAdminBrand2 theBrand = theProduct.getBrand();
	//	List<EntityAdminCategory2> theCategories = theProduct.getCategories();
		
		EntityAdminBrand2 dbBrand = currentSession.get(EntityAdminBrand2.class, theBrand.getBrandId());
		List<EntityProduct> dbBrandProducts = dbBrand.getProducts();
		theProduct.setBrand(null);
		dbBrandProducts.add(theProduct);
		
	//	currentSession.save(theProduct);
	//	currentSession.saveOrUpdate(dbBrand);
		
		entityManager.persist(theProduct);
	//	entityManager.persist(dbBrand);
		entityManager.flush();
		
		
	}  */
	
	
	/*	
	
	public EntityProduct save(EntityProduct theProduct) {
		Session currentSession = entityManager.unwrap(Session.class);	
		
	//	EntityProduct dbProduct = currentSession.get(EntityProduct.class, theProduct.getProductId());
		
		Query theQuery = currentSession.createQuery("from EntityProduct where sku=:sku");
		theQuery.setParameter("sku",theProduct.getSku());
		
		List<EntityProduct> checkProduct =  theQuery.getResultList();
		
		if(checkProduct.size() == 0) {
			return entityManager.merge(theProduct);
		}
		
		return null;		
	}
*/	


}
