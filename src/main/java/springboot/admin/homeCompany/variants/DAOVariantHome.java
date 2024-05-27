package springboot.admin.homeCompany.variants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
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
public class DAOVariantHome {
	
	private EntityManager entityManager;
	private String tenantDbUrl;
	private String tenantDbUsername;
	private String tenantDbPassword;

	
	//set up constructor injection
	
	public DAOVariantHome() {	
	}
	
	@Autowired
	public DAOVariantHome(EntityManager theEntityManager, TenantDbProperties tenantDbProperties) {
		
		this.entityManager = theEntityManager;
		this.tenantDbUrl = tenantDbProperties.getTenantsDbUrl();
		this.tenantDbUsername = tenantDbProperties.getTenantDbUsername();
		this.tenantDbPassword = tenantDbProperties.getTenantDbPassword();
	}
	
	public Boolean updateProductVariantByUnit(EntityProductVariantByUnitHome variant) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query theQuery = (Query) entityManager.createQuery("from EntityProductVariantHome where productId=:productId");
		theQuery.setParameter("productId", variant.getProduct().getProductId());
		
		EntityProductVariantHome dbProduct = (EntityProductVariantHome) theQuery.getSingleResult();
		
		Boolean bulkListChangeBool = false;
		
		if(dbProduct.getBulkList() != variant.getProduct().getBulkList()) {
			bulkListChangeBool = true;
		}
		
		entityManager.merge(variant);
		entityManager.flush();	
		
		return bulkListChangeBool;
		
	}

	public void updateProductInTenants(EntityProductVariantHome theProduct, Boolean bulkListChangeBool) throws Exception {
		
		Session currentSession = entityManager.unwrap(Session.class);		
		EntityProductVariantHome dbProduct = currentSession.get(EntityProductVariantHome.class, theProduct.getProductId());
		
		entityManager.refresh(dbProduct); //product being fetched from cache which does not have complete data. hence refresh..

		if(dbProduct.getTenants() != null) {
			for(EntityTenantVariantHome tenant : dbProduct.getTenants()) {
				
	        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/" + tenant.getTenantUrl() +  "?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
	    		String username = tenantDbUsername;
	    		String password = tenantDbPassword;
							
	    		try (
	    			    Connection connection = DriverManager.getConnection(url, username, password);
	    			    Statement statement = connection.createStatement()
	    			) {
		            
		            //first find product row and update product row
		            
		            if(dbProduct.getDescription() == null) {
		            	dbProduct.setDescription("");
		            }
		            if(dbProduct.getHsnCode() == null) {
		            	dbProduct.setHsnCode("");
		            }
		            if(dbProduct.getSortOrder() == null) {
		            	dbProduct.setSortOrder(100);
		            } 
		            if(dbProduct.getMetaTitle() == null) {
		            	dbProduct.setMetaTitle("");
		            }
		            if(dbProduct.getMetaDescription() == null) {
		            	dbProduct.setMetaDescription("");
		            }
		            if(dbProduct.getMetaKeywords() == null) {
		            	dbProduct.setMetaKeywords("");
		            }
		            if(dbProduct.getSeoURL() == null) {
		            	dbProduct.setSeoURL("");
		            }
		            
		            String updateProductInTenant = "";
		            
		            if(dbProduct.getSortOrder() != null) {
		            	updateProductInTenant = "UPDATE products" + 
		            		" SET product_name = \"" + dbProduct.getProductName() + "\", " +
		            		"description = \"" + dbProduct.getDescription() + "\", " +
		            		"has_expiry_date = " + dbProduct.getHasExpiryDate() + ", " +
		            		"hsn_code = \"" + dbProduct.getHsnCode() + "\", " +
		            		"gst = " + dbProduct.getGst() + ", " +
		            		"sort_order = " + dbProduct.getSortOrder() + ", " +
		            		"meta_title = \"" + dbProduct.getMetaTitle() + "\", " +
		            		"meta_description = \"" + dbProduct.getMetaDescription() + "\", " +
		            		"meta_keywords = \"" + dbProduct.getMetaKeywords() + "\", " +
		            		"seo_url = \"" + dbProduct.getSeoURL() + "\" " +
		            		"WHERE product_home_id = " + dbProduct.getProductId() + ";";
		            }
		            else {
		            	updateProductInTenant = "UPDATE products" + 
		            		" SET product_name = \"" + dbProduct.getProductName() + "\", " +
		            		"description = \"" + dbProduct.getDescription() + "\", " +
		            		"has_expiry_date = " + dbProduct.getHasExpiryDate() + ", " +
		            		"hsn_code = \"" + dbProduct.getHsnCode() + "\", " +
		            		"gst = " + dbProduct.getGst() + ", " +
		            		"meta_title = \"" + dbProduct.getMetaTitle() + "\", " +
		            		"meta_description = \"" + dbProduct.getMetaDescription() + "\", " +
		            		"meta_keywords = \"" + dbProduct.getMetaKeywords() + "\", " +
		            		"seo_url = \"" + dbProduct.getSeoURL() + "\" " +		            		
		            		"WHERE product_home_id = " + dbProduct.getProductId() + ";";
		            	
		            }

		            statement.executeUpdate(updateProductInTenant);
		            
		            //find product_id in tenant from product_home_id
		            
		            Integer productIdInTenant = 0;
		            
		            String findProductIdInTenant = "SELECT product_id FROM products WHERE product_home_id = " + dbProduct.getProductId() + "; ";
		            
		            ResultSet resultSet = statement.executeQuery(findProductIdInTenant);
		            
		            if (resultSet.next()) {
		                productIdInTenant = resultSet.getInt("product_id");
		            }
		            
		            //update categories now..
		            
		            //first find list of oldCategoryIds and newCategoryIds in tenant..
		            		            
		            List<Integer> categoryHomeIds = new ArrayList<>();
		            
		            for(EntityCategoryVariantHome category : dbProduct.getCategories()) {
		            	categoryHomeIds.add(category.getCategoryId());
		            }
		            
		            List<Integer> newCategoryIdsInTenant = new ArrayList<>();
		            
		            for(Integer catHomeId : categoryHomeIds) {
		            	String findCategoryIdInTenant = "SELECT category_id FROM categories WHERE category_home_id = " + catHomeId + ";";
		            	
		            	ResultSet resultSetCatId = statement.executeQuery(findCategoryIdInTenant);		            	
		            	if(resultSetCatId.next()) {
		            		newCategoryIdsInTenant.add(resultSetCatId.getInt("category_id"));
		            	}
		            }
		          		            
		            List<Integer> oldCategoryIdsInTenant = new ArrayList<>();
		            
		            String findOldCategoryIdInTenant = "SELECT category_id FROM product_category WHERE product_id = " + productIdInTenant + ";";
		            
		            ResultSet resultSetOldCatId = statement.executeQuery(findOldCategoryIdInTenant);
		            
		            while(resultSetOldCatId.next()) {
		            	oldCategoryIdsInTenant.add(resultSetOldCatId.getInt("category_id"));
		            }
		            
		            //now, check whether old category ids exist in new category ids. if not, remove the entry from table
		            
		            for(Integer oldCatId : oldCategoryIdsInTenant) {
		            	
		            	Boolean oldCatIdFound = false; 
		            	for(Integer newCatId : newCategoryIdsInTenant) {		            		          		       		
		            		if(oldCatId == newCatId) {
		            			oldCatIdFound = true;
		            		}	
		            	}
		            	
		            	if(oldCatIdFound == false) {  //remove this entry from table		            		
		            		String removeOldCatIdEntry = "DELETE FROM product_category WHERE product_id = " + productIdInTenant +  " AND category_id = "  + oldCatId + ";";
		            		statement.executeUpdate(removeOldCatIdEntry);		            		
		            	}
		            }
		            
		           //new check whether all new category id entries are there in table. if not, add the new entries
		            
		            for(Integer newCatId : newCategoryIdsInTenant) {
		            	
		            	Boolean newCatIdFound = false;
		            	for(Integer oldCatId : oldCategoryIdsInTenant) {
		            		if(oldCatId == newCatId) {
		            			newCatIdFound = true;
		            		}
		            	}
		            	
		            	if(newCatIdFound == false) { //add this entry in table
		            		String addNewCatIdEntry = "INSERT INTO product_category(product_id, category_id) VALUES (" + productIdInTenant +  " , "  + newCatId + " );";
		            		statement.executeUpdate(addNewCatIdEntry);
		            	}
		            }
		            
		            
		            //now, find out new variants that need to be added in tenant and also update existing variants
		            
		            
		            List<EntityProductVariantByUnitHome> variantsInHome = dbProduct.getProductVariantsByUnit();
		            
		            for(EntityProductVariantByUnitHome variant : variantsInHome) {
		            	
		            	if(variant.getVariantActiveStatus() == true) { //this variant should be updated in tenant, if not already there, add it
		            		
		            		Integer variantHomeId = variant.getVariantId();
		            		
				            String findVariantHomeIdInTenant = "SELECT variant_id FROM product_variant_by_unit WHERE variant_home_id = " + variantHomeId + " LIMIT 1;";
				            
				            ResultSet resultSetVariantIdInTenant = statement.executeQuery(findVariantHomeIdInTenant);
				            
				            Integer variantIdInTenant = 0;
				            
				            if(variant.getImagesOrder() == null) {
				            	variant.setImagesOrder("");
				            }
				            if(variant.getKgPiecesName() == null) {
				            	variant.setKgPiecesName("");
				            }
				            if(variant.getKgPiecesPerUnit() == null) {
				            	variant.setKgPiecesPerUnit(0F);
				            }
				             
				            if(resultSetVariantIdInTenant.next()) {
				            	//variant already exists in tenant, so update the data in the variant now..
				            	
				            	variantIdInTenant = resultSetVariantIdInTenant.getInt("variant_id");
				            	
				            	String updateVariantInTenant = "UPDATE product_variant_by_unit " + 
					            		" SET images_order = \"" + variant.getImagesOrder() + "\", " +
					            		"kg_pieces_name = \"" + variant.getKgPiecesName() + "\", " +
					            		"kg_pieces_per_unit = " + variant.getKgPiecesPerUnit() + ", " +
					            		"sku = \"" + variant.getSku() + "\", " +
					            		"variant_MRP = " + variant.getVariantMrp() + " " +					            			            		
					            		
					            		"WHERE variant_home_id = " + variantHomeId + ";";
				            	
				            	statement.executeUpdate(updateVariantInTenant);
				            	
				            	//now, we also need to update the images
				            	
				            	List<EntityImageVariantHome> imagesInHome = variant.getImages();
				            	
				            	//find if these images are in tenant. If not, then add them
				            	for(EntityImageVariantHome image : imagesInHome) {
				            		
				            		String findImageInTenant = "SELECT image_id FROM images WHERE image_home_id = " + image.getImageId() + " LIMIT 1;";
				            		
				            		ResultSet resultSetImage = statement.executeQuery(findImageInTenant);
				            		Boolean imageExistsInTenant = false;
				            		
				            		if(resultSetImage.next()) {
				            			imageExistsInTenant = true;
				            		}
				            		
				            		if(imageExistsInTenant == false) { //add image in tenant
				            			
					            		String addImageInTenant = "INSERT INTO images(image_home_id, image_name, image_url) VALUES (" + image.getImageId() +  " , \""  + image.getImageName() + "\", \"" + image.getImageUrl() + "\" );";
					            		statement.executeUpdate(addImageInTenant);				            							            							            			
				            		}			            		
				            	}
				            	
				            	//now work on correcting the image-variant mapping	
				            	
					            //first find list of oldImageIds and newImageIds in tenant..
            		            
					            List<Integer> imageHomeIds = new ArrayList<>();
					            
					            for(EntityImageVariantHome image : imagesInHome) {
					            	imageHomeIds.add(image.getImageId());
					            }
					            
					            List<Integer> newImageIdsInTenant = new ArrayList<>();
					            
					            for(Integer imageHomeId : imageHomeIds) {
					            	String findImageIdInTenant = "SELECT image_id FROM images WHERE image_home_id = " + imageHomeId + " LIMIT 1;";
					            	
					            	ResultSet resultSetImageId = statement.executeQuery(findImageIdInTenant);		            	
					            	if(resultSetImageId.next()) {
					            		newImageIdsInTenant.add(resultSetImageId.getInt("image_id"));
					            	}
					            }
					          		            
					            List<Integer> oldImageIdsInTenant = new ArrayList<>();
					            
					            String findOldImageIdInTenant = "SELECT image_id FROM product_variant_by_unit_image WHERE variant_id = " + variantIdInTenant + ";";
					            
					            ResultSet resultSetOldImageId = statement.executeQuery(findOldImageIdInTenant);
					            
					            while(resultSetOldImageId.next()) {
					            	oldImageIdsInTenant.add(resultSetOldImageId.getInt("image_id"));
					            }
					            
					            //now, check whether old image ids exist in new image ids. if not, remove the entry from table
					            
					            for(Integer oldImageId : oldImageIdsInTenant) {
					            	
					            	Boolean oldImageIdFound = false; 
					            	for(Integer newImageId : newImageIdsInTenant) {		            		          		       		
					            		if(oldImageId == newImageId) {
					            			oldImageIdFound = true;
					            		}	
					            	}
					            	
					            	if(oldImageIdFound == false) {  //remove this entry from table		            		
					            		String removeOldImageIdEntry = "DELETE FROM product_variant_by_unit_image WHERE variant_id = " + variantIdInTenant +  " AND image_id = "  + oldImageId + ";";
					            		statement.executeUpdate(removeOldImageIdEntry);		            		
					            	}
					            }
					            
					           //new check whether all new image id entries are there in table. if not, add the new entries
					            
					            for(Integer newImageId : newImageIdsInTenant) {
					            	
					            	Boolean newImageIdFound = false;
					            	for(Integer oldImageId : oldImageIdsInTenant) {
					            		if(oldImageId == newImageId) {
					            			newImageIdFound = true;
					            		}
					            	}
					            	
					            	if(newImageIdFound == false) { //add this entry in table
					            		String addNewImageIdEntry = "INSERT INTO product_variant_by_unit_image(variant_id, image_id) VALUES (" + variantIdInTenant +  " , "  + newImageId + " );";
					            		statement.executeUpdate(addNewImageIdEntry);
					            	}
					            }

				            	
				            }
				            else {
				            	//variant does not exist in tenant, so add the new variant in tenant
				            	
				            	String addVariantInTenant = "INSERT INTO product_variant_by_unit (variant_home_id, variant_product_id, unit, quantity, images_order, "
				            			+ " kg_pieces_name, kg_pieces_per_unit, sku, variant_display, variant_MRP) VALUES ( " + 
				            			variant.getVariantId() + ", " +
				            			productIdInTenant + ", \"" +
				            			variant.getUnit() + "\", " +
				            			0 + ", \"" +
				            			variant.getImagesOrder() + "\", \"" +
				            			variant.getKgPiecesName() + "\", " +
				            			variant.getKgPiecesPerUnit() + ", \"" +
				            			variant.getSku() + "\", " +
				            			false + ", " +
				            			variant.getVariantMrp()  + " ); ";
				            	
				            	statement.executeUpdate(addVariantInTenant);
				            	
				            	//lets find the variant_id in tenant of the newly added variant..
				            	
				            	Integer newVariantIdInTenant = 0;
				            	
				            	String findVariantIdInTenant = "SELECT variant_id FROM product_variant_by_unit WHERE variant_home_id = " + variant.getVariantId() + " LIMIT 1;";
				            	
				            	ResultSet resultSetNewVariantId = statement.executeQuery(findVariantIdInTenant);
				            	
				            	if(resultSetNewVariantId.next()) {
				            		newVariantIdInTenant = resultSetNewVariantId.getInt("variant_id");
				            	}

				            	
				            	//now, add images in tenant if they do not already exist..
				            	
				            	List<Integer> imageHomeIds = new ArrayList<>();
				            	
				            	for(EntityImageVariantHome image : variant.getImages()) {
				            		
				            		imageHomeIds.add(image.getImageId());
				            		
				            		String findImageInTenant = "SELECT image_id FROM images WHERE image_home_id = " + image.getImageId() + " LIMIT 1;";
				            		
				            		ResultSet resultSetImage = statement.executeQuery(findImageInTenant);
				            		Boolean imageExistsInTenant = false;
				            		
				            		if(resultSetImage.next()) {
				            			imageExistsInTenant = true;
				            		}
				            		
				            		if(imageExistsInTenant == false) { //add image in tenant
				            			
					            		String addImageInTenant = "INSERT INTO images(image_home_id, image_name, image_url) VALUES (" + image.getImageId() +  " , \""  + image.getImageName() + "\", \"" + image.getImageUrl() + "\" );";
					            		statement.executeUpdate(addImageInTenant);				            							            							            			
				            		}			            		
				            	}
				            	
				            	//now, find the image ids of the images to be associated with the new variant..
				            	
				            	List<Integer> imageIds = new ArrayList<>();
				            	
				            	for(Integer imgHomeId : imageHomeIds) {
				            		String findImagesIdsInTenant = "SELECT image_id from images where image_home_id = " + imgHomeId  +  " LIMIT 1 ;";
				            		
				            		ResultSet resultSetImageIds = statement.executeQuery(findImagesIdsInTenant);
				            		
				            		if(resultSetImageIds.next()) {
				            			imageIds.add(resultSetImageIds.getInt("image_id"));
				            		}
				            	}
				            	
				            	//now add the imageId and variantId entries in table for many to many mapping
				            	
				            	for(Integer imageId : imageIds) {				            		
				            		String addNewImageVariantEntry = "INSERT INTO product_variant_by_unit_image(variant_id, image_id) VALUES ("  + newVariantIdInTenant + ", " + imageId + ")";				            		
				            		statement.executeUpdate(addNewImageVariantEntry);
				            	}
				             	
				            	
				            	//now we need to add an initial stock purchase batch entry
				            	
				            	//first find the variant_id of the newly added variant
				            	
				            	Integer batchVariantId = 0;
				            	
				            	String findBatchVariantId = "SELECT variant_id FROM product_variant_by_unit WHERE variant_home_id = " + variant.getVariantId();
				            	
				            	ResultSet resultSetBatchVariantId = statement.executeQuery(findBatchVariantId);
				            	
				            	if(resultSetBatchVariantId.next()) {
				            		batchVariantId = resultSetBatchVariantId.getInt("variant_id");
				            	}
				            	
				            	//now find supplier_id for supplier- Initial Stock
				            	
				            	Integer supplierId = 0;
				            	
				            	String findSupplierId = "SELECT supplier_id FROM suppliers WHERE supplier_name = \"Initial Stock\" ;"; 
				            	
				            	ResultSet resultSetSupplierId = statement.executeQuery(findSupplierId);
				            	
				            	if(resultSetSupplierId.next()) {
				            		supplierId = resultSetSupplierId.getInt("supplier_id");
				            	}
				            	
				            	//now find the last initial stock purchase invoice id 
				            	
				            	Integer purchaseInvoiceId = 0;

				            	String findInitialStockPurchaseInvoiceId = " SELECT pur_invoice_id FROM purchase_invoices WHERE supplier_id =  " + supplierId + " ORDER BY pur_invoice_id DESC LIMIT 1";
				            	
				            	ResultSet resultSetInitialStockPurchaseInvoiceId = statement.executeQuery(findInitialStockPurchaseInvoiceId);
				            	
				            	if(resultSetInitialStockPurchaseInvoiceId.next()) {
				            		purchaseInvoiceId = resultSetInitialStockPurchaseInvoiceId.getInt("pur_invoice_id");
				            	}
				            		
				            	// now create batch_no 
				            	
				                LocalDate currentDate = LocalDate.now();
				                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				                String batchNo = currentDate.format(formatter);
				                
				                //now create expiry date which is 500 years from today's date
				                
				                Period period = Period.ofYears(500);
				                LocalDate futureDate = currentDate.plus(period);				               
				                String expiryDate = futureDate.format(formatter);
				                
				            	
				            	//now create initial stick purchase entry
				            					            	
				            	String addInitialPurchaseEntry = "INSERT INTO batches (batch_product_id, batch_variant_id, batch_product_home_id, batch_variant_home_id,"
				            			+ " batch_product_name, batch_unit, batch_brand_name, batch_purchase_invoice_id, batch_no, display, "
				            			+ " batch_pur_sale_bool, quantity, current_quantity, batch_MRP, selling_price, batch_purchase_price, pp_includes_gst, expiry_date) VALUES ( " + 
				            			productIdInTenant + ", " +
				            			batchVariantId + ", " +
				            			dbProduct.getProductId() + ", " +
				            			variant.getVariantId() + ", " +
				            			"\"" + dbProduct.getProductName() + "\", " +
				            			"\"" + variant.getUnit() + "\", " +
				            			"\"" + dbProduct.getBrand().getBrandName() + "\", " +
				            			purchaseInvoiceId + ", \"" +
				            			batchNo + "\", " +
				            			true + ", " +
				            			0 + ", " +
				            			0 + ", " +
				            			0 + ", " +
				            			variant.getVariantMrp() + ", " +
				            			variant.getVariantMrp() + ", " +
				            			0 + ", " +
				            			false + ", \"" +
				            			expiryDate + "\" ); ";
				            	
				            	statement.executeUpdate(addInitialPurchaseEntry);
				            }		
		            	}		            			            	
		            }		            
		       }
		       catch(Exception e) {
		    	 //  e.printStackTrace();
		    	   throw e;
		       }					
			}
		}
		
		//now update bulkList variable in unlisted products of tenants
		
	//	System.out.println("bulkListChangeBool: " + bulkListChangeBool);
		
		if(bulkListChangeBool) {
		
			Query theQuery = (Query) entityManager.createQuery("from EntityTenantVariantHome");	
			List<EntityTenantVariantHome> tenants = theQuery.getResultList();
			
			tenants.removeAll(dbProduct.getTenants());
			
			for(EntityTenantVariantHome tenant : tenants) {
				
	        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/" + tenant.getTenantUrl() +  "?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
	    		String username = tenantDbUsername;
	    		String password = tenantDbPassword;
				
	    		try (
	    			    Connection connection = DriverManager.getConnection(url, username, password);
	    			    Statement statement = connection.createStatement()
	    			) {
		            
            		String updateBulkListVariable = "UPDATE unlisted_products SET bulk_list = " + dbProduct.getBulkList() + " WHERE unlisted_product_home_id = " + dbProduct.getProductId() + ";";
            		
            		statement.executeUpdate(updateBulkListVariable);       
		       }
		       catch(Exception e) {
		    	   throw e;
		       }  
			}			
		}		
	}
	
	
	public void productVariantToggleStatus(Integer variantId) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		EntityProductVariantByUnitHome dbVariant = currentSession.get(EntityProductVariantByUnitHome.class, variantId);		
		dbVariant.setVariantActiveStatus(!dbVariant.getVariantActiveStatus());
		
		entityManager.merge(dbVariant);		
	}
	
	/*
	public EntityProductVariantHome save(EntityProductVariantHome theProduct) {
		Session currentSession = entityManager.unwrap(Session.class);	
		
	//	EntityProduct dbProduct = currentSession.get(EntityProduct.class, theProduct.getProductId());
		
		Boolean checkDuplicateSKU = false;
		
		for(EntityProductVariantByUnitHome variant : theProduct.getProductVariantsByUnit()) {
		
			Query theQuery = currentSession.createQuery("from EntityProductVariantByUnitHome where sku=:sku");
			theQuery.setParameter("sku",variant.getSku());
			
			List<EntityProductVariantByUnitHome> variantsWithSKU = theQuery.getResultList();
		
			if(variantsWithSKU.size() > 0) {
				checkDuplicateSKU = true;
			}
		}
		
		if(checkDuplicateSKU == false) {
			
			List<EntityProductVariantByUnitHome> variants = theProduct.getProductVariantsByUnit();
			
			theProduct.setProductVariantsByUnit(null);
			
			EntityProductVariantHome dbProduct = entityManager.merge(theProduct);
			entityManager.flush();
			
			for(EntityProductVariantByUnitHome variant : variants) {			
				variant.setProduct(dbProduct);
				entityManager.merge(variant);
				entityManager.flush();
			}	
			
	//		EntityProductHome dbProduct = entityManager.merge(theProduct);
	//		entityManager.flush();

			Query theQueryTenants = currentSession.createQuery("from EntityTenantHome");
			
			List<EntityTenantVariantHome> dbTenants = theQueryTenants.getResultList();
			
			for(EntityTenantVariantHome tenant : dbTenants) {
			
		       try {       	
		        	String url = "jdbc:mysql://localhost:3306/" + tenant.getTenantUrl() +  "?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
		    		String username = "root";
		    		String password = "swap4pure123";
		    		Connection connection = DriverManager.getConnection(url, username, password);
		    		
		    //		Connection connection = dataSource.getConnection();
		            Statement statement = connection.createStatement();
		            
			        String addNewUnlistedProductInTenantDb = "INSERT INTO unlisted_products(unlisted_product_home_id, unlisted_product_name, unlisted_product_brand_id) VALUES (" + dbProduct.getProductId() + ", \" " + dbProduct.getProductName() + " \", " + dbProduct.getBrand().getBrandId() + ");"; 	            

		            statement.executeUpdate(addNewUnlistedProductInTenantDb);
		       }
		       catch(Exception e) {
		    	   e.printStackTrace();
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
		Query<EntityProductVariantHome> theQuery = currentSession.createQuery("from EntityProductHome where productDeleteStatus is null OR productDeleteStatus=:productDeleteStatus");
		theQuery.setParameter("productDeleteStatus",false);
		
		theQuery.setFirstResult(initialIndex);
		theQuery.setMaxResults(itemsPerPage); 
				
		//execute the query and get result list
		List<EntityProductVariantHome> products = theQuery.getResultList();
		
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
	
	public EntityProductVariantHome findById(Integer productId) {
		
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		EntityProductVariantHome theProduct = currentSession.get(EntityProductVariantHome.class, productId);
		
		if((theProduct.getProductDeleteStatus()== null || theProduct.getProductDeleteStatus().equals(false)) ) {
			return theProduct;
		}
		else {
			return null;
		}				
	}
	
	
	public Boolean deleteById(int theId) {
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);

		
		//its not possible to delete a product without checking if any batch was created in records of any of the tenants
		//checking this is a massive task. so for the moment, lets disable product delete.
		
		return false;
	}
	

	public EntityProductVariantHome updateEditedProduct(EntityProductVariantHome theProduct) {

		return null;

	}
	
	
	
	
	
	public void updateProductDisplay(EntityProductVariantHome theProduct) {
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		EntityProductVariantHome dbProduct = currentSession.get(EntityProductVariantHome.class, theProduct.getProductId()); 
				
		dbProduct.setDisplay(theProduct.isDisplay());			
		currentSession.saveOrUpdate(dbProduct);		
	}
	
	public ResponseProductsHome findProductsByCategory(Integer categoryId, Integer itemsPerPage, Integer initialIndex ) {
		
		ResponseProductsHome responseProducts = new ResponseProductsHome();
		
		Query theQuery = (Query) entityManager.createQuery("select distinct p from EntityProductHome p join p.categories c where c.categoryId = :categoryId AND (p.productDeleteStatus IS NULL "
															+ "	OR p.productDeleteStatus=:deleteStatus) order by p.sortOrder");
		theQuery.setParameter("categoryId", categoryId); 
		theQuery.setParameter("deleteStatus", false);
		
		theQuery.setFirstResult(initialIndex);
		theQuery.setMaxResults(itemsPerPage); 
		
		List<EntityProductVariantHome> dbProducts = theQuery.getResultList();
		
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
	
	
	public ResponseProductsHome findProductsByBrand(Integer brandId, Integer itemsPerPage, Integer initialIndex ) {
		
		ResponseProductsHome responseProducts = new ResponseProductsHome();
		
		Query theQuery = (Query) entityManager.createQuery("SELECT p FROM EntityProductHome p JOIN p.brand b where b.brandId = :brandId AND (p.productDeleteStatus IS NULL OR p.productDeleteStatus=:deleteStatus)");
		theQuery.setParameter("brandId", brandId); 
		theQuery.setParameter("deleteStatus", false);
		
		theQuery.setFirstResult(initialIndex);
		theQuery.setMaxResults(itemsPerPage); 
		
		List<EntityProductVariantHome> dbProducts = theQuery.getResultList();
		
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
	
	public void addProductCategoryEntry(Integer productId, List<Integer> categoryIds) {
		
		Session currentSession = entityManager.unwrap(Session.class);		
		
		EntityProductVariantHome dbProduct = currentSession.get(EntityProductVariantHome.class, productId);
		
		Query theQuery = currentSession.createQuery("from EntityCategoryProductHome", EntityCategoryVariantHome.class);
		List<EntityCategoryVariantHome> dbCategories = theQuery.getResultList();
		
		List<EntityCategoryVariantHome> dbCategoriesInProduct = dbProduct.getCategories();
		
		for (Integer catId : categoryIds) {			
			for(EntityCategoryVariantHome cat : dbCategories) {				
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
		
		EntityProductVariantHome dbProduct = currentSession.get(EntityProductVariantHome.class, productId); 
		EntityCategoryVariantHome dbCategory = currentSession.get(EntityCategoryVariantHome.class, categoryId);
				
		List<EntityCategoryVariantHome> categories = dbProduct.getCategories();	
		// Creating iterator object
        Iterator itrCat = categories.iterator();
        // Holds true till there is single element remaining in the object
        while (itrCat.hasNext()) {        	
        	EntityCategoryVariantHome iterCategory = (EntityCategoryVariantHome) itrCat.next();
            if (iterCategory.getCategoryId() == categoryId) {
                itrCat.remove();
            }
        }
        dbProduct.setCategories(categories);
        currentSession.flush();
        
        List<EntityProductVariantHome> products = dbCategory.getProducts();
		// Creating iterator object
        Iterator itrProd = products.iterator();
  
        // Holds true till there is single element remaining in the object
        while (itrProd.hasNext()) {        	
        	EntityProductVariantHome iterProduct = (EntityProductVariantHome) itrProd.next();
            if (iterProduct.getProductId() == productId) {
                itrProd.remove();
            }
        }
        dbCategory.setProducts(products);
		
		currentSession.saveOrUpdate(dbProduct);	
		currentSession.saveOrUpdate(dbCategory);	
	} 
*/
}
