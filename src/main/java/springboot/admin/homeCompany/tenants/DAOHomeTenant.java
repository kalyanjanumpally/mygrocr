package springboot.admin.homeCompany.tenants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import springboot.admin.homeCompany.customers.EntityCustomerHome;

import org.springframework.security.crypto.password.PasswordEncoder;
import springboot.properties.TenantDbProperties;


@Repository
public class DAOHomeTenant {

	private EntityManager entityManager;
	private String tenantDbUrl;
	private String tenantDbUsername;
	private String tenantDbPassword;
	
    @Autowired
    private DataSource dataSource;
    
	@Autowired
	PasswordEncoder encoder;

	
	//set up constructor injection	
	public DAOHomeTenant() {	
	}
	
	@Autowired
	public DAOHomeTenant(EntityManager theEntityManager, TenantDbProperties tenantDbProperties) {	
		this.entityManager = theEntityManager;
		this.tenantDbUrl = tenantDbProperties.getTenantsDbUrl();
		this.tenantDbUsername = tenantDbProperties.getTenantDbUsername();
		this.tenantDbPassword = tenantDbProperties.getTenantDbPassword();
	}

	 
	public Boolean save(EntityTenant tenant) throws SQLException {			
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query theQuery = currentSession.createQuery("from EntityTenant", EntityTenant.class);
		
		List<EntityTenant> dbTenants = theQuery.getResultList();
		
		Boolean tenantUrlUpdateBool = true;
		
		for(EntityTenant dbTenant : dbTenants) {			
			if(dbTenant.getTenantUrl().equals(tenant.getTenantUrl()) || dbTenant.getTenantPhoneNumber1().equals(tenant.getTenantPhoneNumber1())) {
				tenantUrlUpdateBool = false;
				break;
			}
		}
			
		if(tenantUrlUpdateBool == true) {
			
			entityManager.merge(tenant);
			 
			Query theQueryUnlistedProducts = currentSession.createQuery("from EntityProductTenant order by productId");
				
			List<EntityProductTenant> unlistedProducts = theQueryUnlistedProducts.getResultList();
			 
			Query theQueryBrands = currentSession.createQuery("from EntityBrandTenant order by brandId");
				
			List<EntityBrandTenant> dbBrands = theQueryBrands.getResultList();
				
			Query theQueryCategories = currentSession.createQuery("from EntityCategoryTenant order by categoryId");
				
			List<EntityCategoryTenant> dbCategories = theQueryCategories.getResultList();
			 
			
	        String url = "jdbc:mysql://" + tenantDbUrl + ":3306?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
	    	String username = tenantDbUsername;
	    	String password = tenantDbPassword;
				
    		try (
    			    Connection connection = DriverManager.getConnection(url, username, password);
    			    Statement statement = connection.createStatement()
    			) {
	
	               statement.executeUpdate("CREATE SCHEMA " + tenant.getTenantUrl());
	               statement.executeUpdate("USE " + tenant.getTenantUrl());
	          
	          
	          String createTableBrands = "CREATE TABLE `brands` (\r\n"
	          		+ "  `brand_id` int(11) NOT NULL AUTO_INCREMENT,\r\n"
	          		+ "  `brand_home_id` int(11) DEFAULT NULL,\r\n"  
	          		+ "  `brand_name` varchar(128) DEFAULT NULL,\r\n"
	          		+ "  PRIMARY KEY (`brand_id`),\r\n"
	          		+ "  UNIQUE KEY `UNIQUE_BRAND_NAME` (`brand_name`)\r\n"
	          		+ ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";          		
			statement.execute(createTableBrands);
			/*
	         for(EntityBrandTenant brand : dbBrands) {
	        	 
	        	 String addBrands = "INSERT INTO brands(brand_home_id, brand_name) VALUES "
	        	 		+ "(" + brand.getBrandId() + ", \"" + brand.getBrandName() + "\");";
	        	 statement.execute(addBrands);        	 
	         }
	         */
        	 String addBrand = "INSERT INTO brands(brand_home_id, brand_name) VALUES (?, ?)";
        	 try (PreparedStatement addBrandStatement = connection.prepareStatement(addBrand)) {
        	     for (EntityBrandTenant brand : dbBrands) {
        	         addBrandStatement.setInt(1, brand.getBrandId());
        	         addBrandStatement.setString(2, brand.getBrandName());
        	         addBrandStatement.execute();
        	     }
        	 }
	        
	          
	         String createTableProducts = "CREATE TABLE `products` (\r\n"
	         		+ "  `product_id` INT(11) NOT NULL AUTO_INCREMENT,\r\n"
	         		+ "  `product_home_id` INT(11) DEFAULT NULL,\r\n"
	         		+ "  `product_name` VARCHAR(128) DEFAULT NULL,\r\n"
	         		+ "  `description` BLOB DEFAULT NULL,\r\n"
	         		+ "  `images_order` VARCHAR(128) DEFAULT NULL,\r\n"
	         		+ "  `kg_pieces_name` VARCHAR(128) DEFAULT NULL,\r\n"
	         		+ "  `kg_pieces_per_unit` FLOAT(11) DEFAULT NULL,\r\n"
	         		+ "  `has_expiry_date` BOOLEAN DEFAULT NULL,\r\n"	
	         		+ "  `sku` VARCHAR(255) DEFAULT NULL,\r\n"
	         		+ "  `product_delete_status` BOOL DEFAULT NULL,\r\n"
	         		+ "  `display` BOOLEAN DEFAULT NULL,\r\n"
	         		+ "  `product_brand_id` INT(11) DEFAULT NULL,\r\n"
	         		+ "  `product_MRP` DECIMAL(11) DEFAULT NULL,\r\n"
	         		+ "  `date_created` DATE DEFAULT NULL,\r\n"
	         		+ "  `last_updated` DATE DEFAULT NULL,\r\n"
	         		+ "  `hsn_code` VARCHAR(255) DEFAULT NULL,\r\n"
	         		+ "  `gst` INT(11) DEFAULT NULL,\r\n"
	         		+ "  `sort_order` INT(11) DEFAULT NULL,\r\n"
	         		+ "  `meta_title` VARCHAR(255) DEFAULT NULL,\r\n"
	         		+ "  `meta_description` VARCHAR(255) DEFAULT NULL,\r\n"
	         		+ "  `meta_keywords` VARCHAR(255) DEFAULT NULL,\r\n"
	         		+ "  `seo_url` VARCHAR(255) DEFAULT NULL,\r\n"
	         		+ "  PRIMARY KEY (`product_id`),\r\n"
	         		+ "  KEY `FK_PRODUCT_BRAND_idx` (`product_brand_id`),\r\n"
	         		+ "  CONSTRAINT `FK_PRODUCT_BRAND` FOREIGN KEY (`product_brand_id`) REFERENCES `brands` (`brand_id`) \r\n"
	         		+ ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";          
	         statement.execute(createTableProducts); 
	         
	         
	
	         String createTableUnlistedProducts = "CREATE TABLE `unlisted_products` ("
	        	     + " `unlisted_product_id` INT(11) NOT NULL AUTO_INCREMENT,\r\n"
	        		 + " `unlisted_product_home_id` INT(11) DEFAULT NULL,\r\n"	        	     
					 + " `bulk_list` BOOLEAN DEFAULT NULL,\r\n"	        		 
	        		 + " `unlisted_product_name`VARCHAR(128) DEFAULT NULL,\r\n"
	        		 + "  `unlisted_product_brand_id` INT(11) DEFAULT NULL,\r\n"
	        		 + " PRIMARY KEY (`unlisted_product_id`)\r\n"
	        		 + "  ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";
	         statement.execute(createTableUnlistedProducts); 
	         
	         
	         for(EntityProductTenant unlistedProduct : unlistedProducts) {
	        	 
	        	 String addUnlistedProduct = "INSERT INTO unlisted_products(unlisted_product_home_id, bulk_list, unlisted_product_name, unlisted_product_brand_id) VALUES "
	        	 		+ "(" + unlistedProduct.getProductId() 
	        	 		+ ", " + unlistedProduct.getBulkList()
	        	 		+ ", \"" + unlistedProduct.getProductName() 
	        	 		+ "\", " + unlistedProduct.getBrand().getBrandId() + ");";
	        	 statement.execute(addUnlistedProduct);
	         }
	         
	         
	         String createTableCategories = "CREATE TABLE `categories` (\r\n"
	         		+ "  `category_id` int(11) NOT NULL AUTO_INCREMENT,\r\n"
	         		+ "  `category_home_id` int(11) DEFAULT NULL,\r\n"
	         		+ "  `category_name` varchar(45) DEFAULT NULL,\r\n"
	         		+ "  `parent_category_id` int(11) DEFAULT NULL,\r\n"
	         		+ "  PRIMARY KEY (`category_id`)\r\n"
	         		+ ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";
	         statement.execute(createTableCategories);
	         
	         for(EntityCategoryTenant category : dbCategories) {
	        	 
	        	 String addCategories = "INSERT INTO categories(category_home_id, category_name, parent_category_id) VALUES "
	        	 		+ "(" + category.getCategoryId() + ", \"" + category.getCategoryName() + "\", " + category.getParentCategoryId() + ");";
	        	 statement.execute(addCategories);
	         }
	         
	         
	         String createTableProduct_Category = "CREATE TABLE `product_category` (\r\n"
	         		+ "  `product_id` INT(11) NOT NULL,\r\n"
	         		+ "  `category_id` INT(11) NOT NULL,\r\n"
	         		+ "  PRIMARY KEY (`product_id`,`category_id`),\r\n"
	         		+ "  KEY `FK_PROD_CAT_idx` (`product_id`),\r\n"
	         		+ "  CONSTRAINT `FK_CATEGORY` FOREIGN KEY (`category_id`) \r\n"
	         		+ "  REFERENCES `categories` (`category_id`) \r\n"
	         		+ "  ON DELETE NO ACTION ON UPDATE NO ACTION,\r\n"
	         		+ "  CONSTRAINT `FK_PROD_CAT` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) \r\n"
	         		+ "  ON DELETE NO ACTION ON UPDATE NO ACTION\r\n"
	         		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
	         statement.execute(createTableProduct_Category);
	         
	         
	         String createTableImages = "CREATE TABLE `images` (\r\n"
	         		+ "  `image_id` int(11) NOT NULL AUTO_INCREMENT,\r\n"
	         		+ "  `image_home_id` int(11) DEFAULT NULL,\r\n" 
	         		+ "  `image_name` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  `image_url` varchar(256) DEFAULT NULL,\r\n"
	         		+ "  PRIMARY KEY (`image_id`)\r\n"
	         		+ ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";
	         statement.execute(createTableImages);
	         
	         
	          String createTableVariantsByUnit = "CREATE TABLE `product_variant_by_unit` ( \r\n"
	        		  + "  `variant_id` int(11) NOT NULL AUTO_INCREMENT, \r\n"
	        		  + " `variant_home_id` int(11) DEFAULT NULL, \r\n"
	        		  + " `variant_product_id` int(11) DEFAULT NULL, \r\n"
	        		  + " `unit` varchar(128) DEFAULT NULL, \r\n"
	        		  + " `quantity` float(11) DEFAULT NULL, \r\n"
		         	  + " `display_out_of_stock_variant` BOOLEAN DEFAULT NULL,\r\n"
	        		  + " `images_order` VARCHAR(128) DEFAULT NULL, \r\n"
	        		  + " `kg_pieces_name` VARCHAR(128) DEFAULT NULL, \r\n"
	        		  + " `kg_pieces_per_unit` FLOAT(11) DEFAULT NULL, \r\n"
	        		  + " `sku` VARCHAR(255) DEFAULT NULL, \r\n"
	        		  + " `variant_display` BOOLEAN DEFAULT NULL, \r\n"
	        		  + " `variant_MRP` DECIMAL(11) DEFAULT NULL, \r\n"
	        		  + " PRIMARY KEY (`variant_id`), \r\n"
	        		  + " KEY `FK_PRODUCT_VARIANT` (`variant_product_id`), \r\n"
	        		  + " CONSTRAINT `FK_PRODUCT_VARIANT` FOREIGN KEY (`variant_product_id`) REFERENCES `products` (`product_id`) \r\n"
	        		  + " ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";
	         statement.execute(createTableVariantsByUnit);
	         
	         
	         String createTableProduct_Image = "CREATE TABLE `product_variant_by_unit_image` (\r\n"
		         		+ "  `variant_id` INT(11) NOT NULL,\r\n"
		         		+ "  `image_id` INT(11) NOT NULL,\r\n"
		         		+ "  PRIMARY KEY (`variant_id`,`image_id`),\r\n"
		         		+ "  KEY `FK_PROD_VAR_IMA_idx` (`variant_id`),\r\n"
		         		+ "  CONSTRAINT `FK_IMAGE` FOREIGN KEY (`image_id`) REFERENCES `images` (`image_id`) \r\n"
		         		+ "  ON DELETE NO ACTION ON UPDATE NO ACTION,\r\n"
		         		+ "  CONSTRAINT `FK_PROD_VAR_IMG` FOREIGN KEY (`variant_id`) REFERENCES `product_variant_by_unit` (`variant_id`) \r\n"
		         		+ "  ON DELETE NO ACTION ON UPDATE NO ACTION\r\n"
		         		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
		         statement.execute(createTableProduct_Image);
	         
	         String createTableBatches = "CREATE TABLE `batches` (\r\n"
	         		+ "  `batch_id` int(11) NOT NULL AUTO_INCREMENT,\r\n"
	         		+ "  `batch_product_id` int(11) DEFAULT NULL,\r\n"
	         		+ "  `batch_variant_id` int(11) DEFAULT NULL,\r\n"
	         		+ "  `batch_product_home_id` int(11) DEFAULT NULL,\r\n"
	         		+ "  `batch_variant_home_id` int(11) DEFAULT NULL,\r\n"
	         		+ "  `batch_product_name` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  `batch_unit` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  `batch_brand_name` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  `batch_purchase_invoice_id` bigint(11) DEFAULT NULL,\r\n"
	         		+ "  `batch_order_id` bigint(11) DEFAULT NULL,\r\n"
	         		+ "  `transaction_status` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  `batch_no` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  `display` int(11) DEFAULT NULL,\r\n"
	         		+ "  `batch_pur_sale_bool` int(11) DEFAULT NULL,\r\n"
	         		+ "  `batch_entry_date` date DEFAULT NULL,\r\n"
	         		+ "  `quantity` float(11) DEFAULT NULL,\r\n"
	         		+ "  `current_quantity` float(11) DEFAULT NULL,\r\n"
	         		+ "  `batch_MRP` float(11) DEFAULT NULL,\r\n"
	         		+ "  `selling_price` float(11) DEFAULT NULL,\r\n"
	         		+ "  `batch_purchase_price` float(11) DEFAULT NULL,\r\n"
	         		+ "  `pp_includes_gst` bool DEFAULT NULL,\r\n"
	         		+ "  `batch_gst` int(11) DEFAULT NULL,\r\n"	         		
	         		+ "  `expiry_date` date DEFAULT NULL, \r\n"
	         		+ "  PRIMARY KEY (`batch_id`),\r\n"
	         		+ "KEY `FK_PRODUCT_BATCH_idx` (`batch_product_id`),\r\n"
	         		+ "  CONSTRAINT `FK_PRODUCT_BATCH`\r\n"
	         		+ "  FOREIGN KEY(`batch_product_id`)\r\n"
	         		+ "  REFERENCES `products` (`product_id`) \r\n"
	         		+ "  ON DELETE NO ACTION ON UPDATE NO ACTION\r\n"
	         		+ ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";
	         statement.execute(createTableBatches);
	         
	         
	         String createTableCustomers = "CREATE TABLE `customers` (\r\n"
	         		+ "  `customer_id` int(11) NOT NULL AUTO_INCREMENT,\r\n"
	         		+ "  `customer_home_id` int(11) DEFAULT NULL,\r\n"
	         		+ "  `customer_delete_status` bool DEFAULT NULL,\r\n"
	         		+ "  `customer_active` bool DEFAULT NULL,\r\n"
	         		+ "  `customer_type` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  `first_name` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  `last_name` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  `full_name` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  `company_name` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  `phone_no_1` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  `date_registered` date DEFAULT NULL,\r\n"
	         		+ "  `phone_no_2` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  `email` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  `password` varchar(128) DEFAULT NULL, #should be encrypted\r\n"
	         		+ "  `address` BLOB DEFAULT NULL,\r\n"
	         		+ "  `postal_code` varchar(128) DEFAULT NULL, \r\n"
	         		+ "  `city` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  `state` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  PRIMARY KEY (`customer_id`)\r\n"
	         		+ ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";
	         statement.execute(createTableCustomers);
	         
	         
	         String createTableAddress = "CREATE TABLE `address` (\r\n"
	          		+ "  `address_id` int NOT NULL AUTO_INCREMENT,\r\n"
	          		+ "  `city` varchar(255) DEFAULT NULL,\r\n"
	          		+ "  `country` varchar(255) DEFAULT NULL,\r\n"
	          		+ "  `state` varchar(255) DEFAULT NULL,\r\n"
	          		+ "  `street` varchar(255) DEFAULT NULL,\r\n"
	          		+ "  `zip_code` varchar(255) DEFAULT NULL,\r\n"
	          		+ "  PRIMARY KEY (`address_id`)\r\n"
	          		+ ") ENGINE=InnoDB AUTO_INCREMENT=1 AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";
	          statement.execute(createTableAddress);
	         
	         
	         String createTableOrders = "CREATE TABLE `orders` (\r\n"
	         		+ "  `order_id` bigint NOT NULL AUTO_INCREMENT,\r\n"
	         		+ "  `number_of_batches` int DEFAULT NULL,\r\n"
	         		+ "  `sales_return_id` bigint DEFAULT NULL,\r\n"
	         		+ "  `order_delivery_status` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  `order_source_type` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  `sub_total` float DEFAULT NULL,\r\n"
	         		+ "  `pending_payment` float DEFAULT NULL,\r\n"
	         		+ "  `shipping_charges` int(11) DEFAULT NULL,\r\n"
	         		+ "  `date_time_created` datetime DEFAULT NULL,\r\n"
	         		+ "  `date_created` date DEFAULT NULL,\r\n"
	         		+ "  `date_time_delivered` datetime DEFAULT NULL,\r\n"
	         		+ "  `customer_id` int DEFAULT NULL,\r\n"
	         		+ "  `shipping_address_id` int DEFAULT NULL,\r\n"
	         		+ "  `comments` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  PRIMARY KEY (`order_id`),\r\n"
	         		+ "  UNIQUE KEY `UK_shipping_address_id` (`shipping_address_id`),\r\n"
	         		+ "  KEY `K_customer_id` (`customer_id`),\r\n"
	         		+ "  CONSTRAINT `FK_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),\r\n"
	         		+ "  CONSTRAINT `FK_shipping_address_id` FOREIGN KEY (`shipping_address_id`) REFERENCES `address` (`address_id`)\r\n"
	         		+ ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";
	         statement.execute(createTableOrders);
	         
	         
	         String createTableCustomerPayments = "CREATE TABLE `customer_payments` (\r\n"
	         		+ "  `payment_id` bigint NOT NULL AUTO_INCREMENT,\r\n"
	         		+ "  `payment_order_id` bigint DEFAULT NULL,\r\n"
	         		+ "  `amount` float DEFAULT NULL,\r\n"
	         		+ "  `payment_mode` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  `payment_date` date DEFAULT NULL,\r\n"
	         		+ "  PRIMARY KEY (`payment_id`),\r\n"
	         		+ "  CONSTRAINT `FK_order_id` FOREIGN KEY (`payment_order_id`) REFERENCES `orders` (`order_id`)\r\n"
	         		+ ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";
	         statement.execute(createTableCustomerPayments);
	         
	         
	         String createTableSuppliers = "CREATE TABLE `suppliers` (\r\n"
	          		+ "  `supplier_id` int(11) NOT NULL AUTO_INCREMENT,\r\n"
	          		+ "  `supplier_name` varchar(128) DEFAULT NULL,\r\n"
	          		+ "  `contact_person` varchar(128) DEFAULT NULL,\r\n"
	          		+ "  `phone_no_1` varchar(128) DEFAULT NULL,\r\n"
	          		+ "  `phone_no_2` varchar(128) DEFAULT NULL, \r\n"
	          		+ "  `email` varchar(128) DEFAULT NULL,\r\n"
	          		+ "  `address` varchar(128) DEFAULT NULL, \r\n"
	          		+ "  PRIMARY KEY (`supplier_id`)\r\n"
	          		+ ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";
	          statement.execute(createTableSuppliers);
	          
	         
	         String createTablePurchaseInvoices = "CREATE TABLE `purchase_invoices` (\r\n"
	         		+ "  `pur_invoice_id` bigint(11) NOT NULL AUTO_INCREMENT,\r\n"
	         		+ "  `number_of_batches` int DEFAULT NULL,\r\n"	        		 
	         		+ "  `supplier_id` int(11) DEFAULT NULL,\r\n"
	         		+ "  `pur_return_id` bigint(11) DEFAULT NULL,\r\n"
	         		+ "  `purchase_delivery_status` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  `date_created` date DEFAULT NULL,\r\n"
	         		+ "  `amount` float(11) DEFAULT NULL,\r\n"
	         		+ "  `payment_details` varchar(128) DEFAULT NULL,  \r\n"
	         		+ "  PRIMARY KEY (`pur_invoice_id`),\r\n"
	         		+ "  KEY `FK_SUPP_INVOICE_idx` (`supplier_id`),\r\n"
	         		+ "  CONSTRAINT `FK_SUPP_INVOICE` FOREIGN KEY(`supplier_id`) REFERENCES `suppliers` (`supplier_id`) \r\n"
	         		+ "  ON DELETE NO ACTION ON UPDATE NO ACTION\r\n"
	         		+ ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";
	         statement.execute(createTablePurchaseInvoices);
	         
	         
	         String createTableCountry = "CREATE TABLE `country` (\r\n"
	         		+ "  `country_id` smallint unsigned NOT NULL,\r\n"
	         		+ "  `code` varchar(2) DEFAULT NULL,\r\n"
	         		+ "  `name` varchar(255) DEFAULT NULL,\r\n"
	         		+ "  PRIMARY KEY (`country_id`)\r\n"
	         		+ ") ENGINE=InnoDB;";
	         statement.execute(createTableCountry);
	         
	         
	         String createTableState = "CREATE TABLE `state` (\r\n"
	         		+ "  `state_id` smallint unsigned NOT NULL AUTO_INCREMENT,\r\n"
	         		+ "  `name` varchar(255) DEFAULT NULL,\r\n"
	         		+ "  `country_id` smallint unsigned NOT NULL,\r\n"
	         		+ "  PRIMARY KEY (`state_id`),\r\n"
	         		+ "  KEY `fk_country` (`country_id`),\r\n"
	         		+ "  CONSTRAINT `fk_country` FOREIGN KEY (`country_id`) REFERENCES `country` (`country_id`)\r\n"
	         		+ ") ENGINE=InnoDB AUTO_INCREMENT=1;";
	         statement.execute(createTableState);
	         
	         
	         String createTableEmployees = "CREATE TABLE `employees` (\r\n"
	         		+ "  `employee_id` int(11) NOT NULL AUTO_INCREMENT,\r\n"
	         		+ "  `tenant_url` varchar(128) DEFAULT NULL,\r\n" 
	         		+ "  `username` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  `password` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  `employee_active` BOOLEAN,\r\n"
	         		+ "  PRIMARY KEY (`employee_id`)\r\n"
	         		+ ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";
	         statement.execute(createTableEmployees);
	         
	         
	         String createTableRoles = "CREATE TABLE `roles` (\r\n"
	         		+ "  `role_id` int(11) NOT NULL AUTO_INCREMENT,\r\n"
	         		+ "  `role_name` varchar(128) DEFAULT NULL,\r\n"
	         		+ "  PRIMARY KEY (`role_id`)\r\n"
	         		+ ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1; ";
	         statement.execute(createTableRoles);
	         
	         
	         String createTableEmployee_Roles = "CREATE TABLE `employees_roles` (\r\n"
	         		+ "  `employee_id` INT(11) NOT NULL,\r\n"
	         		+ "  `role_id` INT(11) NOT NULL,\r\n"
	         		+ "  PRIMARY KEY (`employee_id`,`role_id`),\r\n"
	         		+ "  KEY `FK_EMP_ROLE_idx` (`employee_id`),\r\n"
	         		+ "  CONSTRAINT `FK_ROLE` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) \r\n"
	         		+ "  ON DELETE NO ACTION ON UPDATE NO ACTION,\r\n"
	         		+ "  CONSTRAINT `FK_EMP_ROLE` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) \r\n"
	         		+ "  ON DELETE NO ACTION ON UPDATE NO ACTION\r\n"
	         		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
	         statement.execute(createTableEmployee_Roles);
	         
	         String insertDataRoles1 = "INSERT INTO roles(role_name) VALUES('ROLE_USER');";
	         statement.execute(insertDataRoles1);
	         
	         String insertDataRoles2 = "INSERT INTO roles(role_name) VALUES('ROLE_MANAGER');";
	         statement.execute(insertDataRoles2);
	         
	         String insertDataRoles3 = "INSERT INTO roles(role_name) VALUES('ROLE_ADMIN');";
	         statement.execute(insertDataRoles3);
	         
	         String insertDataEmployees = "INSERT INTO employees(employee_id, tenant_url, employee_active, username, password) VALUES(1, \"" + tenant.getTenantUrl() + "\", true, 'admin', '$2a$10$mT7rZLIXGATMMWTgFTWz6OEAzy/IiLUwPUg8YUI1xYSwnhjNvbRi2' );\r\n";
	         statement.execute(insertDataEmployees); 
	         
	         String insertDataEmployeesRoles = "INSERT INTO employees_roles(employee_id, role_id) VALUES(1, 2);\r\n";
	         statement.execute(insertDataEmployeesRoles); 
	         
	         } catch (SQLException e) {
	             //  e.printStackTrace();
	        	 throw e;
	         }
		}
		return tenantUrlUpdateBool;
	}
	
	
	public void editTenant(EntityTenant tenant) {
		
		entityManager.merge(tenant);
		
	}
	
	/*
	public void updateTenantDetailsByTenant(DTOTenantDetails tenantDetails) {
		
		Session currentSession = entityManager.unwrap(Session.class);	
		
		Query<EntityTenant> theQuery = currentSession.createQuery("from EntityTenant WHERE tenantUrl =:tenantUrl");
		theQuery.setParameter("tenantUrl", tenantDetails.getTenantUrl());
		
		EntityTenant dbTenant = theQuery.getSingleResult();	
		
		dbTenant.setShopOpenTime(tenantDetails.getShopOpenTime());
		dbTenant.setShopCloseTime(tenantDetails.getShopCloseTime());
		dbTenant.setTenantOpen(tenantDetails.getTenantOpen());
		
		entityManager.merge(dbTenant);		
	}
	*/

	
	public Boolean editTenantUrl(EntityTenant tenant) throws SQLException {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query theQuery = currentSession.createQuery("from EntityTenant", EntityTenant.class);
		
		List<EntityTenant> dbTenants = theQuery.getResultList();
		
		Boolean tenantUrlUpdateBool = true;
		
		for(EntityTenant dbTenant : dbTenants) {			
			if(dbTenant.getTenantUrl().equals(tenant.getTenantUrl())) {
				tenantUrlUpdateBool = false;
				break;
			}
		}	
			
		if(tenantUrlUpdateBool == true) {
			EntityTenant dbTenantToUpdate = entityManager.find(EntityTenant.class, tenant.getTenantId());
			
			String oldTenantUrl = dbTenantToUpdate.getTenantUrl();
			
			dbTenantToUpdate.setTenantUrl(tenant.getTenantUrl());
			entityManager.merge(dbTenantToUpdate);
			
        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
    		String username = tenantDbUsername;
    		String password = tenantDbPassword;
			
	        try(
		    		Connection connection = DriverManager.getConnection(url, username, password);			    		
		            Statement statement = connection.createStatement();		
	        ) {
	            
	            String createTenantDb = "CREATE DATABASE " + tenant.getTenantUrl();
	            statement.execute(createTenantDb); 
	            /*
	            String renameAddress = "RENAME TABLE " +  oldTenantUrl + ".address TO " + tenant.getTenantUrl() +  ".address;";
	            statement.execute(renameAddress); 
	            
	            String renameBatches = "RENAME TABLE " +  oldTenantUrl + ".batches TO " + tenant.getTenantUrl() +  ".batches;";
	            statement.execute(renameBatches); 
	            
	            String renameBrands = "RENAME TABLE " +  oldTenantUrl + ".brands TO " + tenant.getTenantUrl() +  ".brands;";
	            statement.execute(renameBrands); 
	            
	            String renameCategories = "RENAME TABLE " +  oldTenantUrl + ".categories TO " + tenant.getTenantUrl() +  ".categories;";
	            statement.execute(renameCategories); 
	            
	            String renameCountry = "RENAME TABLE " +  oldTenantUrl + ".country TO " + tenant.getTenantUrl() +  ".country;";
	            statement.execute(renameCountry); 
	            
	            String renameCustomerPayments = "RENAME TABLE " +  oldTenantUrl + ".customer_payments TO " + tenant.getTenantUrl() +  ".customer_payments;";
	            statement.execute(renameCustomerPayments); 
	            
	            String renameCustomers = "RENAME TABLE " +  oldTenantUrl + ".customers TO " + tenant.getTenantUrl() +  ".customers;";
	            statement.execute(renameCustomers); 
	            
	            String renameEmployees = "RENAME TABLE " +  oldTenantUrl + ".employees TO " + tenant.getTenantUrl() +  ".employees;";
	            statement.execute(renameEmployees); 
	            
	            String renameEmployeesRoles = "RENAME TABLE " +  oldTenantUrl + ".employees_roles TO " + tenant.getTenantUrl() +  ".employees_roles;";
	            statement.execute(renameEmployeesRoles); 
	            
	            String renameImages = "RENAME TABLE " +  oldTenantUrl + ".images TO " + tenant.getTenantUrl() +  ".images;";
	            statement.execute(renameImages); 
	            
	            String renameOrders = "RENAME TABLE " +  oldTenantUrl + ".orders TO " + tenant.getTenantUrl() +  ".orders;";
	            statement.execute(renameOrders); 
	            
	            String renameProductCategory = "RENAME TABLE " +  oldTenantUrl + ".product_category TO " + tenant.getTenantUrl() +  ".product_category;";
	            statement.execute(renameProductCategory); 
	            
	            String renameProductVariantByUnit = "RENAME TABLE " +  oldTenantUrl + ".product_variant_by_unit TO " + tenant.getTenantUrl() +  ".product_variant_by_unit;";
	            statement.execute(renameProductVariantByUnit);    
	            
	            String renameProductImage = "RENAME TABLE " +  oldTenantUrl + ".product_variant_by_unit_image TO " + tenant.getTenantUrl() +  ".product_variant_by_unit_image;";
	            statement.execute(renameProductImage); 
	            
	            String renameProducts = "RENAME TABLE " +  oldTenantUrl + ".products TO " + tenant.getTenantUrl() +  ".products;";
	            statement.execute(renameProducts); 
	            
	            String renamePurchaseInvoices = "RENAME TABLE " +  oldTenantUrl + ".purchase_invoices TO " + tenant.getTenantUrl() +  ".purchase_invoices;";
	            statement.execute(renamePurchaseInvoices); 
	            
	            String renameRoles = "RENAME TABLE " +  oldTenantUrl + ".roles TO " + tenant.getTenantUrl() +  ".roles;";
	            statement.execute(renameRoles); 
	            
	            String renameState = "RENAME TABLE " +  oldTenantUrl + ".state TO " + tenant.getTenantUrl() +  ".state;";
	            statement.execute(renameState); 
	            
	            String renameSuppliers = "RENAME TABLE " +  oldTenantUrl + ".suppliers TO " + tenant.getTenantUrl() +  ".suppliers;";
	            statement.execute(renameSuppliers); 
	            
	            String renameUnlistedProducts = "RENAME TABLE " +  oldTenantUrl + ".unlisted_products TO " + tenant.getTenantUrl() +  ".unlisted_products;";
	            statement.execute(renameUnlistedProducts); 
	            */
	            
	            // Define an array of tables to be renamed
	            String[] tablesToRename = {"address", "batches", "brands", "categories", "country", "customer_payments", "customers",
	                    "employees", "employees_roles", "images", "orders", "product_category", "product_variant_by_unit",
	                    "product_variant_by_unit_image", "products", "purchase_invoices", "roles", "state", "suppliers",
	                    "unlisted_products"};

	            // Rename tables from oldTenantUrl to tenant.getTenantUrl()
	            for (String tableName : tablesToRename) {
	                String renameTable = "RENAME TABLE " + oldTenantUrl + "." + tableName + " TO " + tenant.getTenantUrl() + "." + tableName;
	                statement.execute(renameTable);
	            }
	            
	            String dropDb = "DROP DATABASE " + oldTenantUrl; 
	            statement.execute(dropDb); 
	            

			} catch (SQLException e) {
				throw e;
			}	
		}
			
		return tenantUrlUpdateBool;	
	}
	
	 
	public EntityTenant findById(Integer tenantId) { //this is generic method for all order source types
		
		Session currentSession = entityManager.unwrap(Session.class);		
		return currentSession.get(EntityTenant.class, tenantId); 			
	}
	
	public EntityTenant findByTenantUrl(String tenantUrl) {
		
		Session currentSession = entityManager.unwrap(Session.class);	
		
		Query<EntityTenant> theQuery = currentSession.createQuery("from EntityTenant WHERE tenantUrl =:tenantUrl");
		theQuery.setParameter("tenantUrl", tenantUrl);
		
		EntityTenant dbTenant = theQuery.getSingleResult();
		
		return dbTenant;
	}
	
	
	 
	public List<EntityTenant> findAllTenants(Integer itemsPerPage, Integer startIndex) {		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query<EntityTenant> theQuery =
				  currentSession.createQuery("from EntityTenant ORDER BY tenantId DESC", EntityTenant.class);	
		
		theQuery.setFirstResult(startIndex);
		theQuery.setMaxResults(itemsPerPage); 		
		
		//execute the query and get result list 
		List<EntityTenant> dbTenants = theQuery.getResultList();
				  
	    //return result 
		return dbTenants; 
	}
	

	 
	public Long countOfAllTenants() {		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query countQuery =
				  currentSession.createQuery("select count(t.tenantId) from EntityTenant t"); 
		
		Long countResults =  (Long) countQuery.uniqueResult();
	
				  
	    //return result 
		return countResults; 
	}

	 
	public EntityTenant checkTenantActiveStatus(String tenantUrl) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
	//	DTOTenantStatus dTOTenantStatus = new DTOTenantStatus();
		
		Query<EntityTenant> theQuery =
				  currentSession.createQuery("from EntityTenant where tenantUrl=:tenantUrl", EntityTenant.class);
		theQuery.setParameter("tenantUrl", tenantUrl);
		
		EntityTenant dbTenant = theQuery.getSingleResult();

		return dbTenant;
	}
	
	
	 
	public EntityTenant checkTenantActiveStatusFromId(Integer tenantId) {
		
	//	DTOTenantStatus dTOTenantStatus = new DTOTenantStatus();
		
		EntityTenant dbTenant = (EntityTenant) entityManager.find(EntityTenant.class, tenantId);

		return dbTenant;
	}

	 
	public List<EntityTenant> fetchStoresInLocation() {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
	//	DTOTenantStatus dTOTenantStatus = new DTOTenantStatus();
		
		Query<EntityTenant> theQuery =
				  currentSession.createQuery("from EntityTenant", EntityTenant.class);
		
		List<EntityTenant> dbTenants = theQuery.getResultList();
		
		return dbTenants;
	}

	public Boolean resetTenantEmployeePassword(DTOEmployeeTenantPasswordReset dTOPasswordReset) throws SQLException {
		
		Session currentSession = entityManager.unwrap(Session.class);		
	//	EntityEmployeeTenant dbEmployeeTenant = currentSession.get(EntityEmployeeTenant.class, dTOPasswordReset.getTenantId());
		
		Boolean passwordUpdateSuccess = true;
		
    	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/" + dTOPasswordReset.getTenantUrl() + "?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
		String username = tenantDbUsername;
		String password = tenantDbPassword;
		
        try(
	    		Connection connection = DriverManager.getConnection(url, username, password);			    		
	            Statement statement = connection.createStatement();		
        ) {
            
            String newEncodedPassword = encoder.encode(dTOPasswordReset.getNewPassword());
               
            String updateEmployeePasswordInTenantsDB = "UPDATE employees SET password = \"" + newEncodedPassword + "\" WHERE employee_id = 1";
            statement.execute(updateEmployeePasswordInTenantsDB);     		
        } 
        catch (SQLException e) {
        	passwordUpdateSuccess = false;
             throw e;
              
        }
        
        return passwordUpdateSuccess;
		
	}

}
