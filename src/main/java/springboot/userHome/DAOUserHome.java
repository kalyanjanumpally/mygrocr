package springboot.userHome;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import springboot.admin.homeCompany.customers.EntityTenantCustomer;
import springboot.properties.TenantDbProperties;


@Repository
public class DAOUserHome {
	
	@Autowired
	private EntityManager entityManager;
	
	private String tenantDbUrl;
	private String tenantDbUsername;
	private String tenantDbPassword;
	
	public DAOUserHome() {	
	}
	
	
	@Autowired
	public DAOUserHome(EntityManager theEntityManager, TenantDbProperties tenantDbProperties) {
		
		this.entityManager = theEntityManager;
		this.tenantDbUrl = tenantDbProperties.getTenantsDbUrl();
		this.tenantDbUsername = tenantDbProperties.getTenantDbUsername();
		this.tenantDbPassword = tenantDbProperties.getTenantDbPassword();
	}
	

	public ResponseOrdersUserHome findOrdersOfCustomer(Integer customerId, Integer itemsPerPage, Integer startIndex) throws SQLException {

		ResponseOrdersUserHome responseOrders = new ResponseOrdersUserHome();
		
		Session currentSession = entityManager.unwrap(Session.class);
			
		Query countQuery =
				  currentSession.createQuery("select count(orderId) from EntityOrderUserHome where"
				  		+ " customer.customerId =:customerId");
		countQuery.setParameter("customerId",customerId);	
		
		Long countResults =  (Long) countQuery.uniqueResult();	
		
		responseOrders.setCountOfOrders(countResults);		
		
		
		Query theQuery =  currentSession.createQuery("from EntityOrderUserHome where "
				  		+ "customer.customerId =:customerId order by orderId desc");
		theQuery.setParameter("customerId",customerId);
		
		theQuery.setFirstResult(startIndex);
		theQuery.setMaxResults(itemsPerPage); 		
		
		//execute the query and get result list 
		List<EntityOrderUserHome> dbOrdersHome = theQuery.getResultList();
		
		System.out.println(dbOrdersHome);
		
		List<DtoOrdersAndBatchesUserHome> dtoOrders = new ArrayList<>();
		
		for(EntityOrderUserHome dbOrder : dbOrdersHome) {
			
			Query theQueryTenantData =  currentSession.createQuery("select tenantUrl, tenantName from EntityTenantUserHome where tenantId =:tenantId");
			theQueryTenantData.setParameter("tenantId",dbOrder.getTenantId());
			
	//		String tenantUrl = (String) theQueryTenantUrl.getSingleResult();
			
			Object[] tenantData =  (Object[]) theQueryTenantData.getSingleResult();
			
			String tenantUrl = (String) tenantData[0];
			String tenantName = (String) tenantData[1];	
			
			DtoOrdersAndBatchesUserHome dtoOrderAndBatches = new DtoOrdersAndBatchesUserHome();
			
			DtoOrderUserHome dtoOrder = new DtoOrderUserHome();
			List<DtoBatchAndProductVariantUserHome> dtoBatches = new ArrayList<>();
			
	        try {       	
	        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/" + tenantUrl + "?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
	    		String username = tenantDbUsername;
	    		String password = tenantDbPassword;
	    		Connection connection = DriverManager.getConnection(url, username, password);
	    		
	            Statement statement = connection.createStatement();
	            
	            //get tenant_id from tenant_url and save in a variable
	            
	            String getOrder = "SELECT orders.*, payment_mode FROM orders LEFT JOIN (SELECT payment_order_id, payment_mode, payment_id FROM customer_payments ORDER BY payment_id DESC) p ON p.payment_order_id = orders.order_id  WHERE orders.order_id = " + dbOrder.getOrderNo() + ";";
	            
	            ResultSet resultSet = statement.executeQuery(getOrder); 
	            
                if (resultSet.next()) {
                	dtoOrder.setTenantId(dbOrder.getTenantId());
                	dtoOrder.setTenantName(tenantName);
                	dtoOrder.setTenantUrl(tenantUrl);
                	dtoOrder.setOrderId(resultSet.getLong("order_id")); 
                	dtoOrder.setOrderHomeId(dbOrder.getOrderId());
                	dtoOrder.setNumberOfBatches(resultSet.getInt("number_of_batches"));
                	dtoOrder.setDateTimeDelivered(resultSet.getDate("date_time_delivered"));
                	dtoOrder.setDateTimeCreated(resultSet.getTimestamp("date_time_created"));
                	dtoOrder.setOrderDeliveryStatus(resultSet.getString("order_delivery_status"));
                	dtoOrder.setOrderSourceType(resultSet.getString("order_source_type"));
                	dtoOrder.setPendingPayment(resultSet.getFloat("pending_payment"));
                	dtoOrder.setSalesReturnId(resultSet.getLong("sales_return_id"));
                	dtoOrder.setShippingAddressId(resultSet.getInt("shipping_address_id"));
                	dtoOrder.setShippingCharges(resultSet.getInt("shipping_charges"));
                	dtoOrder.setSubTotal(resultSet.getFloat("sub_total"));
                	dtoOrder.setComments(resultSet.getString("comments"));
                	dtoOrder.setPaymentMode(resultSet.getString("payment_mode"));
                } 
                
                
                /*
	            String getBatches = "SELECT batches.*, product_name, brand_name, unit FROM batches LEFT JOIN products ON batches.batch_product_id = products.product_id LEFT JOIN "
	            		+ " brands ON products.product_brand_id = brands.brand_id LEFT JOIN product_variant_by_unit ON batches.batch_variant_id = product_variant_by_unit.variant_id "
	            		+ "WHERE batch_order_id = " + dbOrder.getOrderNo() + " ORDER BY batch_id DESC LIMIT " + dbOrder.getNumberOfBatches() + ";";
	            */
	            
                
	            String getBatches = "SELECT * FROM batches WHERE batch_order_id = " + dbOrder.getOrderNo() 
	                               + " AND (transaction_status IS NULL OR (transaction_status is NOT NULL AND transaction_status != 'batch-deleted')) "
	                               + "ORDER BY batch_id DESC LIMIT " + dtoOrder.getNumberOfBatches() + ";";
                
	            
	            ResultSet resultSetBatches = statement.executeQuery(getBatches); 
 
	            while (resultSetBatches.next()) {
	            	
	            	DtoBatchAndProductVariantUserHome batch = new DtoBatchAndProductVariantUserHome();
	            	
                	batch.setBatchId(resultSetBatches.getLong("batch_id"));
                	batch.setBatchNo(resultSetBatches.getString("batch_no"));
                	batch.setBatchOrderId(resultSetBatches.getInt("batch_order_id"));
                	batch.setBatchBrandName(resultSetBatches.getString("batch_brand_name"));
                	batch.setBatchProductId(resultSetBatches.getInt("batch_product_id"));
                	batch.setBatchVariantId(resultSetBatches.getInt("batch_variant_id"));
                	batch.setBatchProductName(resultSetBatches.getString("batch_product_name"));
                	batch.setBatchUnit(resultSetBatches.getString("batch_unit"));
                	batch.setBatchPurchasePrice(resultSetBatches.getFloat("batch_purchase_price"));
                	batch.setBatchPurSaleBool(resultSetBatches.getInt("batch_pur_sale_bool"));
                	batch.setExpiryDate(resultSetBatches.getDate("expiry_date"));
                	batch.setMrp(resultSetBatches.getFloat("batch_MRP"));
                	batch.setPpIncludesGST(resultSetBatches.getBoolean("pp_includes_gst"));
                	batch.setBatchGST(resultSetBatches.getInt("batch_gst"));
                	batch.setQuantity(resultSetBatches.getFloat("quantity"));
                	batch.setSellingPrice(resultSetBatches.getInt("selling_price"));
                	batch.setTransactionStatus(resultSetBatches.getString("transaction_status"));

                	dtoBatches.add(batch);
                }
	            
	            Collections.reverse(dtoBatches);
	            
	           // System.out.println(dtoBatches);
	            
	            dtoOrderAndBatches.setBatches(dtoBatches);
	            dtoOrderAndBatches.setOrder(dtoOrder);
	            
	            dtoOrders.add(dtoOrderAndBatches);
   
	        } 
	        catch (SQLException e) {
	                     e.printStackTrace();
	                     throw e;
	        }	
			
	        responseOrders.setDtos(dtoOrders);
		}		
		return responseOrders;
	}


	public void saveOnlineOrderInTenant(DtoOnlineOrderDataUserHome dtoOrder) throws Exception {
		
		//first, check if customer exists in tenant. if not, then save new customer

		Session currentSession = entityManager.unwrap(Session.class);
		
		EntityTenantUserHome dbTenant = entityManager.find(EntityTenantUserHome.class, dtoOrder.getTenantId());
		
        try {       	
        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/" + dbTenant.getTenantUrl() + "?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
    		String username = tenantDbUsername;
    		String password = tenantDbPassword;
    		Connection connection = DriverManager.getConnection(url, username, password);
    		
            Statement statement = connection.createStatement();
            
        	Long orderId = (long) 0;
        	Integer customerIdInTenant = 0;
        	Date date = new Date();
        	
            // Convert java.util.Date to java.sql.Timestamp
            java.sql.Timestamp timestamp = new java.sql.Timestamp(System.currentTimeMillis());

            // Format the java.sql.Timestamp to string
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            String dateTimeString = dateTimeFormat.format(timestamp);
            
            //get if customer exists in tenant db-
            
            System.out.println("customer id: " + dtoOrder.getDtoCustomer().getCustomerId());
            System.out.println("phone no: " + dtoOrder.getDtoCustomer().getPhoneNo1());
            System.out.println("email: " + dtoOrder.getDtoCustomer().getEmail());
            
            String getCustomerHomeId = "SELECT customer_id FROM customers  WHERE customer_home_id = " + dtoOrder.getDtoCustomer().getCustomerId() + " OR phone_no_1 = \"" 
            							+ dtoOrder.getDtoCustomer().getPhoneNo1() + "\" OR email = \"" + dtoOrder.getDtoCustomer().getEmail() + "\" LIMIT 1;";
            
            ResultSet resultSet = statement.executeQuery(getCustomerHomeId); 
            
            if(resultSet.next()) {
            	customerIdInTenant = resultSet.getInt("customer_id");
            }
            
            if(customerIdInTenant == 0) { // customer does not exist in tenant db, so first add the customer in tenant db..
            	
            	Date todayDate = new Date();
            	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                String dateString = dateFormat.format(date);
            	
            	EntityCustomerUserHome dbCustomerHome = entityManager.find(EntityCustomerUserHome.class, dtoOrder.getDtoCustomer().getCustomerId());
            	
            	String addCustomer = "INSERT INTO customers (customer_home_id, first_name, last_name, full_name, phone_no_1, phone_no_2, email, address, postal_code, city, state,  customer_type, date_registered) "
            			+ " VALUES (" + dbCustomerHome.getCustomerId() 
            			+ ", \"" + dbCustomerHome.getFirstName() + "\""
            			+ ", \"" + dbCustomerHome.getLastName() + "\""
            			+ ", \"" + dbCustomerHome.getFullName() + "\""
            			+ ", \"" + dbCustomerHome.getPhoneNo1() + "\"" 
            			+ ", \"" + dbCustomerHome.getPhoneNo2() + "\""
            			+ ", \"" + dbCustomerHome.getEmail() + "\""
            			+ ", \"" + dbCustomerHome.getAddress() + "\""
            			+ ", \"" + dbCustomerHome.getPostalCode() + "\""
            			+ ", \"" + dbCustomerHome.getCity() + "\""
            			+ ", \"" + dbCustomerHome.getState() + "\""
            			+ ", \"" + dbCustomerHome.getCustomerType() + "\""
            			+ ", \"" + dateString + "\""
            			
            			+ ")";

            	statement.executeUpdate(addCustomer);
            	
            	//now get the customer_id of the newly created customer in tenant..           	
            	String getCustomerId = "SELECT customer_id FROM customers WHERE customer_home_id = " + dtoOrder.getDtoCustomer().getCustomerId() + " LIMIT 1";
            	
            	ResultSet resultSetCustomer = statement.executeQuery(getCustomerId);
            	
            	Integer customerId = 0;
            	
            	if(resultSetCustomer.next()) {
            		customerId = resultSetCustomer.getInt("customer_id");
            	}
            	
            	//now add order & batches in tenant db, update quantity of variant, add entries in Payments by calling a method-           	
            	orderId = addOrderBatchesUpdateVariantStockAndPaymentEntryInTenantDb(dtoOrder, statement, dateTimeString, customerId);            	

            }
            else {  //customer already exists in tenant db, but customerHomeId may have to be updated..
            	
            	String updateCustomerHomeId = "UPDATE customers SET customer_home_id = " + dtoOrder.getDtoCustomer().getCustomerId() + " WHERE customer_id = " + customerIdInTenant;
            	statement.executeUpdate(updateCustomerHomeId);
            	
            	//now add order & batches in tenant db, update quantity of variant, add entries in Payments by calling a method-
            	orderId = addOrderBatchesUpdateVariantStockAndPaymentEntryInTenantDb(dtoOrder, statement, dateTimeString, customerIdInTenant);
            	
            }
            
            //now add order in home company..
            
    		EntityCustomerUserHome dbCustomer = entityManager.find(EntityCustomerUserHome.class, dtoOrder.getDtoCustomer().getCustomerId());
    		
    		List<EntityTenantUserHome> dbCustomerTenants = dbCustomer.getTenants();
    		
    		Boolean customerTenantMapped = false;
    		
    		for(EntityTenantUserHome tenant : dbCustomerTenants) {
    			if(tenant.getTenantId() == dbTenant.getTenantId()) {
    				customerTenantMapped = true;
    			}   			
    		}
    		
    		if(customerTenantMapped == false) {
    			dbCustomerTenants.add(dbTenant);
    		}
    		
    		EntityOrderUserHome homeOrder = new EntityOrderUserHome();
    		
    		homeOrder.setCustomer(dbCustomer);    		
    		homeOrder.setDateTimeCreated(date);   		
    		homeOrder.setOrderNo(orderId);
    		homeOrder.setTenantId(dtoOrder.getTenantId());
    		homeOrder.setOrderDeliveryStatus(dtoOrder.getOrder().getOrderDeliveryStatus());
    		
    		entityManager.merge(homeOrder);
	
        } 
        catch (Exception e) {
           //          e.printStackTrace();
                     throw e;
        }	
		
	}
	
	
	public Long addOrderBatchesUpdateVariantStockAndPaymentEntryInTenantDb(DtoOnlineOrderDataUserHome dtoOrder, Statement statement, String dateTimeString, Integer customerId) throws SQLException {
		
		Long orderId = (long) 0;
				
    	if(dtoOrder.getOrder().getComments() != null) {
        	
        	String addOrderWithComments = "INSERT INTO orders (order_source_type, order_delivery_status, sub_total, pending_payment, "
        			+ "shipping_charges, date_time_created, customer_id, comments, number_of_batches) VALUES ("  
        			+ " \"" + dtoOrder.getOrder().getOrderSourceType() + "\""
        			+ ", \"" + dtoOrder.getOrder().getOrderDeliveryStatus() + "\""
        			+ ", " + dtoOrder.getOrder().getSubTotal() 
        			+ ", " + dtoOrder.getOrder().getPendingPayment() 
        			+ ", " + dtoOrder.getOrder().getShippingCharges() 
        			+ ", \"" + dateTimeString + "\""
        			+ ", " + customerId
        			+ ", \"" + dtoOrder.getOrder().getComments() + "\""
        			+ ", " + dtoOrder.getOrder().getNumberOfBatches() 
        			+ ");";
        	statement.executeUpdate(addOrderWithComments);
    	}
    	else {                	
        	String addOrderWithoutComments = "INSERT INTO orders (order_source_type, order_delivery_status, sub_total, pending_payment, "
        			+ "shipping_charges, date_time_created, customer_id, numbet_of_batches) VALUES ("  
        			+ " \"" + dtoOrder.getOrder().getOrderSourceType() + "\""
        			+ ", \"" + dtoOrder.getOrder().getOrderDeliveryStatus() + "\""
        			+ ", " + dtoOrder.getOrder().getSubTotal() 
        			+ ", " + dtoOrder.getOrder().getPendingPayment() 
        			+ ", " + dtoOrder.getOrder().getShippingCharges() 
        			+ ", \"" + dateTimeString + "\""
        		    + ", " + customerId
        			+ ", " + dtoOrder.getOrder().getNumberOfBatches()  
        			+ ");";  
        	statement.executeUpdate(addOrderWithoutComments);
    	}
    	
    	//now get orderId
    	
    	String getOrderId = "SELECT order_id FROM orders WHERE customer_id = " + customerId + " ORDER BY order_id DESC LIMIT 1";
    	
    	ResultSet resultSetOrderId = statement.executeQuery(getOrderId);
    	
    	if(resultSetOrderId.next()) {
    		orderId = resultSetOrderId.getLong("order_id");
    	}           		
    	
    	//now add batches in tenant db
    	
    	for(DtoBatchAndProductVariantUserHome dtoBatch : dtoOrder.getBatches()) {
    		String insertBatches = "INSERT INTO batches (batch_product_id, batch_variant_id, batch_product_home_id, batch_variant_home_id, "
    				+ " batch_product_name, batch_unit, batch_brand_name, batch_order_id, "
    				+ "batch_no, batch_pur_sale_bool, quantity, batch_MRP, selling_price, batch_purchase_price, pp_includes_gst, batch_gst, expiry_date) VALUES( "
    				+ dtoBatch.getBatchProductId() 
    				+ ", " + dtoBatch.getBatchVariantId() 
    				+ ", " + dtoBatch.getBatchProductHomeId()
    				+ ", " + dtoBatch.getBatchVariantHomeId()            				
    				+ ", \"" + dtoBatch.getBatchProductName() + "\""
    				+ ", \"" + dtoBatch.getBatchUnit() + "\""
    				+ ", \"" + dtoBatch.getBatchBrandName() + "\""
    				+ ", " + orderId  
    				+ ", \"" + dtoBatch.getBatchNo() + "\""
    				+ ", " + dtoBatch.getBatchPurSaleBool() 
    				+ ", " + dtoBatch.getQuantity() 
    				+ ", " + dtoBatch.getMrp() 
    				+ ", " + dtoBatch.getSellingPrice() 
    				+ ", " + dtoBatch.getBatchPurchasePrice()
    				+ ", " + dtoBatch.getPpIncludesGST() 
    				+ ", " + dtoBatch.getBatchGST()
    				+ ", \"" + dtoBatch.getExpiryDate() + "\""
    				+ ");";
    		
    		statement.executeUpdate(insertBatches);  		
    	}
    	
       	//now, lets update current stock in variant and batch:
    	
    	for(DtoBatchAndProductVariantUserHome dtoBatch : dtoOrder.getBatches()) {
    	            	
    		String getVariantQuantity = "SELECT quantity FROM product_variant_by_unit WHERE variant_id = " + dtoBatch.getBatchVariantId();
    		
    		ResultSet resultSetVariantQuantity = statement.executeQuery(getVariantQuantity);
    		
    		Float variantQuantity = 0F;
    		
    		if(resultSetVariantQuantity.next()) {
    			variantQuantity = resultSetVariantQuantity.getFloat("quantity");
    		}
    		
    		Float updatedVariantQuantity = variantQuantity - dtoBatch.getQuantity();
    		
    		String updateVariantQuantityInDb = "UPDATE product_variant_by_unit SET quantity = " + updatedVariantQuantity + " WHERE variant_id = " + dtoBatch.getBatchVariantId();
    		statement.executeUpdate(updateVariantQuantityInDb);
    		
    		String getPurchaseBatchCurrentQuantity = "SELECT current_quantity FROM batches WHERE batch_variant_id = " + dtoBatch.getBatchVariantId() + " AND batch_pur_sale_bool = 0 LIMIT 1";
    		
    		ResultSet resultSetCurrentQuantity = statement.executeQuery(getPurchaseBatchCurrentQuantity);
    		
    		Float currentQuantity = 0F;
    		
    		if(resultSetCurrentQuantity.next()) {
    			currentQuantity = resultSetCurrentQuantity.getFloat("current_quantity");
    		}
    		
    		Float updatedCurrentQuantity = currentQuantity - dtoBatch.getQuantity();
    		
    		String updatePurchaseBatchCurrentQuantity = "UPDATE batches SET current_quantity = " + updatedCurrentQuantity + " WHERE batch_variant_id = " + dtoBatch.getBatchVariantId() 
    		  + " AND batch_pur_sale_bool = 0 LIMIT 1";
    		
    		statement.executeUpdate(updatePurchaseBatchCurrentQuantity);            		
    	}
    	
    	// add payments details in tenant db..
    	
	    long millis = System.currentTimeMillis();  
		java.sql.Date todayDate = new java.sql.Date(millis);
		
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayDateString = dateFormat.format(todayDate);
    	
    	for(DTOCustomerPaymentsUserHome payment : dtoOrder.getOrder().getPayments()) {
    		
    		String insertPaymentsData = "INSERT INTO customer_payments (payment_order_id, amount, payment_mode, payment_date) VALUES ("
    				+ orderId 
    				+ ", " + payment.getAmount() 
    				+ ", \"" + payment.getPaymentMode() + "\""
    				+ ", \"" + todayDateString + "\""
    				+")";           		
    		statement.executeUpdate(insertPaymentsData);           		
    	}            	  
		
		return orderId;	
		
	}
	
	

	public void cancelOrder(Integer tenantId, Long orderId) throws Exception {

		Session currentSession = entityManager.unwrap(Session.class);
		
		EntityOrderUserHome dbOrder = currentSession.get(EntityOrderUserHome.class, orderId); 				
		dbOrder.setOrderDeliveryStatus("Cancelled");				
		currentSession.saveOrUpdate(dbOrder);
		currentSession.flush();
		
		EntityTenantUserHome dbTenant = entityManager.find(EntityTenantUserHome.class, tenantId);
		
        try {       	
        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/" + dbTenant.getTenantUrl() + "?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
    		String username = tenantDbUsername;
    		String password = tenantDbPassword;
    		Connection connection = DriverManager.getConnection(url, username, password);
    		
            Statement statement = connection.createStatement();
            
            
            //we have to update order, batches and payments in tenant db. first update order
            
            String getOrderData = "SELECT sub_total, shipping_charges, pending_payment, number_of_batches FROM orders WHERE order_id = " + dbOrder.getOrderNo();
            
            ResultSet resultSetGetOrderData = statement.executeQuery(getOrderData);
            
            Float subTotal = 0F;
            Float shippingCharges = 0F;
            Float pendingPayment = 0F;
            Integer numberOfBatches = 0;
            
            if(resultSetGetOrderData.next()) {
            	subTotal = resultSetGetOrderData.getFloat("sub_total");
            	shippingCharges = resultSetGetOrderData.getFloat("shipping_charges");
            	pendingPayment = resultSetGetOrderData.getFloat("pending_payment");
            	numberOfBatches = resultSetGetOrderData.getInt("number_of_batches");
            }
            
            Float updatedPendingPayment = pendingPayment - subTotal - shippingCharges;
            
            String updateOrder = "UPDATE orders SET order_delivery_status = \"Cancelled\", pending_payment = " + updatedPendingPayment + " WHERE order_id = " + dbOrder.getOrderNo();
            statement.executeUpdate(updateOrder);
            
            //now update quantity in variant and batches..
            
        	//now, lets update current quantity in variant and batch:
            
            
            //first get batches from db of this order which do not have status as - batch_cancelled
            
            
            String getBatchesOfOrder = "SELECT quantity, batch_variant_id FROM batches WHERE batch_order_id = " + dbOrder.getOrderNo() 
            							+ " AND (transaction_status IS NULL OR transaction_status <> \"batch_cancelled\") ORDER BY batch_id DESC LIMIT " + numberOfBatches;
            
            ResultSet resultSetBatchesOfOrder = statement.executeQuery(getBatchesOfOrder);
            
            List<Object[]> batchesData = new ArrayList<>();
            
            
            while(resultSetBatchesOfOrder.next()) {           	
            	
            	Integer variantId = resultSetBatchesOfOrder.getInt("batch_variant_id");
            	Float batchQuantity = resultSetBatchesOfOrder.getFloat("quantity");
            	
            	Object[] batches = {variantId, batchQuantity};
            	batchesData.add(batches);
            	
            }
            
            for(Object[] batch : batchesData) {
            	
            	Integer variantId = (Integer) batch[0];
            	Float batchQuantity = (Float) batch[1];
            	
        		String getVariantQuantity = "SELECT quantity FROM product_variant_by_unit WHERE variant_id = " + variantId;
        		
        		ResultSet resultSetVariantQuantity = statement.executeQuery(getVariantQuantity);
        		
        		Float variantQuantity = 0F;
        		
        		if(resultSetVariantQuantity.next()) {
        			variantQuantity = resultSetVariantQuantity.getFloat("quantity");
        		}
        		
        		Float updatedVariantQuantity = variantQuantity + batchQuantity;

        		String updateVariantQuantityInDb = "UPDATE product_variant_by_unit SET quantity = " + updatedVariantQuantity + " WHERE variant_id = " + variantId;
        		
        		statement.executeUpdate(updateVariantQuantityInDb);
        		
        		String getPurchaseBatchCurrentQuantity = "SELECT current_quantity FROM batches WHERE batch_variant_id = " + variantId + " AND batch_pur_sale_bool = 0 LIMIT 1";
        		
        		ResultSet resultSetCurrentQuantity = statement.executeQuery(getPurchaseBatchCurrentQuantity);
        		
        		Float currentQuantity = 0F;
        		
        		if(resultSetCurrentQuantity.next()) {
        			currentQuantity = resultSetCurrentQuantity.getFloat("current_quantity");
        		}
        		
        		Float updatedCurrentQuantity = currentQuantity + batchQuantity;
        		
        		String updatePurchaseBatchCurrentQuantity = "UPDATE batches SET current_quantity = " + updatedCurrentQuantity + " WHERE batch_variant_id = " + variantId 
        		  + " AND batch_pur_sale_bool = 0 LIMIT 1";
        		
        		statement.executeUpdate(updatePurchaseBatchCurrentQuantity);             	 	            	
            }
                        
            //now update batches
            
            String cancelBatches = "UPDATE batches SET transaction_status = \"order-cancelled\" WHERE batch_order_id = " + dbOrder.getOrderNo() + " ORDER BY batch_id LIMIT " + numberOfBatches;
            statement.executeUpdate(cancelBatches);
            
            
         
        } 
        catch (Exception e) {
           //          e.printStackTrace();
                     throw e;
        }	           
	
	}
	
	public Boolean changeDeliveryAddress(EntityCustomerUserHome customer) throws SQLException {

		EntityCustomerUserHome dbCustomer = entityManager.find(EntityCustomerUserHome.class, customer.getCustomerId());
			
		if(dbCustomer == null) {
			return false;     //customer does not exist
		}
		else {
			
			dbCustomer.setAddress(customer.getAddress());
			dbCustomer.setCity(customer.getCity());
			dbCustomer.setPostalCode(customer.getPostalCode());
			dbCustomer.setState(customer.getState());
		}		
		entityManager.merge(dbCustomer);
		
		if(dbCustomer.getTenants() != null) {
			
			for(EntityTenantUserHome tenant : dbCustomer.getTenants()) {
				
		        try {       	
		        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/" + tenant.getTenantUrl() + "?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
		    		String username = tenantDbUsername;
		    		String password = tenantDbPassword;
		    		Connection connection = DriverManager.getConnection(url, username, password);
		    		
		    //		Connection connection = dataSource.getConnection();
		            Statement statement = connection.createStatement();
		            
		            String updateCustomerDetails = "UPDATE customers set first_name = \"" + dbCustomer.getFirstName() + "\"" 
		            												+ ", last_name = \"" + dbCustomer.getLastName() + "\""
		            												+ ", full_name = \"" + dbCustomer.getFullName() + "\""
		            												+ ", company_name = \"" + dbCustomer.getCompanyName() + "\""
		            												+ ", phone_no_1 = \"" + dbCustomer.getPhoneNo1() + "\""
		            												+ ", phone_no_2 = \"" + dbCustomer.getPhoneNo2() + "\""
		            												+ ", email = \"" + dbCustomer.getEmail() + "\""
		            												+ ", address = \"" + dbCustomer.getAddress() + "\""
		            												+ ", postal_code = \"" + dbCustomer.getPostalCode() + "\""
		            												+ ", city = \"" + dbCustomer.getCity() + "\""
		            												+ ", state = \"" + dbCustomer.getState() + "\""
		            												+ " where customer_home_id = " + dbCustomer.getCustomerId();
		            statement.executeUpdate(updateCustomerDetails);
		        } 
		        catch (SQLException e) {
		        	//e.printStackTrace();
		        	throw e;
		        }
			}
		}				

		return true;
	}
	
	public Boolean updateAccount(EntityCustomerUserHome customer) throws SQLException {

		EntityCustomerUserHome dbCustomer = entityManager.find(EntityCustomerUserHome.class, customer.getCustomerId());
		
		if(dbCustomer == null) {
			return false;     //customer does not exist
		}
		else {
			
			dbCustomer.setAddress(customer.getAddress());
			dbCustomer.setCity(customer.getCity());
			dbCustomer.setPostalCode(customer.getPostalCode());
			dbCustomer.setState(customer.getState());
			dbCustomer.setFirstName(customer.getFirstName());
			dbCustomer.setLastName(customer.getLastName());
			dbCustomer.setFullName(customer.getFullName());
			dbCustomer.setPhoneNo1(customer.getPhoneNo1());
			dbCustomer.setPhoneNo2(customer.getPhoneNo2());
			dbCustomer.setEmail(customer.getEmail());
		}		
		entityManager.merge(dbCustomer);	
		
		if(dbCustomer.getTenants() != null) {
			
			for(EntityTenantUserHome tenant : dbCustomer.getTenants()) {
				
		        try {       	
		        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/" + tenant.getTenantUrl() + "?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
		    		String username = tenantDbUsername;
		    		String password = tenantDbPassword;
		    		Connection connection = DriverManager.getConnection(url, username, password);
		    		
		    //		Connection connection = dataSource.getConnection();
		            Statement statement = connection.createStatement();
		            
		            String updateCustomerDetails = "UPDATE customers set first_name = \"" + dbCustomer.getFirstName() + "\"" 
		            												+ ", last_name = \"" + dbCustomer.getLastName() + "\""
		            												+ ", full_name = \"" + dbCustomer.getFullName() + "\""
		            												+ ", company_name = \"" + dbCustomer.getCompanyName() + "\""
		            												+ ", phone_no_1 = \"" + dbCustomer.getPhoneNo1() + "\""
		            												+ ", phone_no_2 = \"" + dbCustomer.getPhoneNo2() + "\""
		            												+ ", email = \"" + dbCustomer.getEmail() + "\""
		            												+ ", address = \"" + dbCustomer.getAddress() + "\""
		            												+ ", postal_code = \"" + dbCustomer.getPostalCode() + "\""
		            												+ ", city = \"" + dbCustomer.getCity() + "\""
		            												+ ", state = \"" + dbCustomer.getState() + "\""
		            												+ " where customer_home_id = " + dbCustomer.getCustomerId();
		            statement.executeUpdate(updateCustomerDetails);
		        } 
		        catch (SQLException e) {
		        	//e.printStackTrace();
		        	throw e;
		        }
			}
		}		
		return true;
	}

/*
	public List<DtoBatchAndProductUserHome> findBatches(int orderId) {
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query theQuery =
				  currentSession.createQuery("select b, p.productName as batchProductName, p.sku as batchProductSku, p.brand as brand from EntityBatchesUser b "
				  		                          + " LEFT OUTER JOIN EntityProductUser p ON b.batchProductId = p.productId "
				  		                          + "	where b.batchOrderId =:batchOrderId ");
		theQuery.setParameter("batchOrderId",orderId);
	//	theQuery.setParameter("transactionStatus","batch-deleted");
		
		List<Object[]> queryResult = theQuery.list();
	    
	    List<DtoBatchAndProductUserHome> batches = new ArrayList<DtoBatchAndProductUserHome>();
	    
	    for(Object[] obj : queryResult) {
	    	
	    	DtoBatchAndProductUserHome batch = new DtoBatchAndProductUserHome();
	    	
	    	batch.setBatchId( ((EntityBatchesUser) obj[0]).getBatchId() );
	    	batch.setBatchProductId( ((EntityBatchesUser) obj[0]).getBatchProductId() );
	    	batch.setBatchNo( ((EntityBatchesUser) obj[0]).getBatchNo() );
	    	batch.setBatchPurSaleBool( ((EntityBatchesUser) obj[0]).getBatchPurSaleBool() );
	    	batch.setQuantity( ((EntityBatchesUser) obj[0]).getQuantity() );
	    	batch.setMrp( ((EntityBatchesUser) obj[0]).getMrp() );
	    	batch.setSellingPrice( ((EntityBatchesUser) obj[0]).getSellingPrice() );
	    	batch.setBatchPurchasePrice( ((EntityBatchesUser) obj[0]).getBatchPurchasePrice() );
	    	batch.setPpIncludesGST( ((EntityBatchesUser) obj[0]).getPpIncludesGST() );
	    	batch.setExpiryDate( ((EntityBatchesUser) obj[0]).getExpiryDate() );
	    	batch.setBatchOrderId( ((EntityBatchesUser) obj[0]).getBatchOrderId() );
	    	batch.setTransactionStatus( ((EntityBatchesUser) obj[0]).getTransactionStatus() );
	    	
	    	batch.setBatchProductName((String)obj[1]);
	    	batch.setBatchProductSku((String) obj[2]);
	    	batch.setBatchProductBrand(( (EntityBrandUser) obj[3]).getBrandName() );
	    	
	    	batches.add(batch);	    	
	    }

	    Iterator itr = batches.iterator();
	    while (itr.hasNext()) { 	
	    	DtoBatchAndProductUserHome dbBatchIter = (DtoBatchAndProductUserHome)itr.next();
	         if (dbBatchIter.getTransactionStatus() != null) {
	        	 if (dbBatchIter.getTransactionStatus().equals("batch-deleted") ) {
	        		 itr.remove();
	        	 }
	         }  
	    } 
		return batches;
	}
*/
	
	
/*
	public Long countOfAllOrdersOfCustomer(Integer customerId) {
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query countQuery =
				  currentSession.createQuery("select count(o.orderId) from EntityOrderUser o JOIN o.customer c where"
				  		+ " c.customerId =:customerId");
		countQuery.setParameter("customerId",customerId);	
		
		Long countResults =  (Long) countQuery.uniqueResult();	
				  
	    //return result 
		return countResults; 
	}
*/	


}
