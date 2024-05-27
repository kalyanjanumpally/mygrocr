package springboot.admin.homeCompany.orders;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.persistence.Query;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import springboot.properties.TenantDbProperties;


@Repository
public class DAOHomeOrders {

	private EntityManager entityManager;
	private String tenantDbUrl;
	private String tenantDbUsername;
	private String tenantDbPassword;
	
    @Autowired
    private DataSource dataSource;
    
    String getOrderDataString = "SELECT orders.*, payment_mode FROM orders LEFT JOIN "
    		+ "(select payment_id, payment_order_id, payment_mode FROM customer_payments ORDER BY payment_id DESC) p"
    		+ " ON orders.order_id = p.payment_order_id WHERE order_id = ";
    
    /*
    String getBatchesDataString = "SELECT batches.*, product_name, brand_name, unit FROM batches"
    		+ " LEFT OUTER JOIN products ON batches.batch_product_id = products.product_id "
    		+ " LEFT OUTER JOIN brands ON products.product_brand_id = brands.brand_id"
    		+ " LEFT OUTER JOIN product_variant_by_unit ON product_variant_by_unit.variant_id = batches.batch_variant_id"
    		+ " WHERE batch_order_id = ";
*/
    
    String getBatchesDataString = "SELECT * FROM batches WHERE batch_order_id = ";
    
    
	
	//set up constructor injection	
	public DAOHomeOrders() {	
	}
	
	@Autowired
	public DAOHomeOrders(EntityManager theEntityManager, TenantDbProperties tenantDbProperties) {	
		this.entityManager = theEntityManager;
		this.tenantDbUrl = tenantDbProperties.getTenantsDbUrl();
		this.tenantDbUsername = tenantDbProperties.getTenantDbUsername();
		this.tenantDbPassword = tenantDbProperties.getTenantDbPassword();
	}

	public ResponseOrdersHome findAllOrders(Integer itemsPerPage, Integer startIndex) throws SQLException {
		
		ResponseOrdersHome responseOrders = new ResponseOrdersHome();

		Session currentSession = entityManager.unwrap(Session.class);
		
		Query countQuery = currentSession.createQuery("select count(orderId) from EntityOrderUserHome");	
		
		Long countResults =  (Long) ((org.hibernate.query.Query) countQuery).uniqueResult();	
		
		responseOrders.setCountOfOrders(countResults);						
		
		Query theQuery = currentSession.createQuery("from EntityOrderHome ORDER BY orderId DESC");	
		
		theQuery.setFirstResult(startIndex);
		theQuery.setMaxResults(itemsPerPage); 
		
		List<EntityOrderHome> dbOrders = theQuery.getResultList();
		
		List<DtoOrderAndBatchesHome> dtoOrdersAndBatches = findBatchesFromOrders(dbOrders);
		
		responseOrders.setDtos(dtoOrdersAndBatches);
		
		return responseOrders;
	}
		

	public Boolean cancelOrder(Long orderId) throws SQLException {
		
	//	Session currentSession = entityManager.unwrap(Session.class);
		
		//Query theQuery = currentSession.createQuery("from EntityOrderHome ORDER BY orderId DESC");	
		
		EntityOrderHome dbOrderHome = entityManager.find(EntityOrderHome.class, orderId);

		if(dbOrderHome.getOrderDeliveryStatus() == "Pending" || dbOrderHome.getOrderDeliveryStatus().equals("Yet to accept")) {

			dbOrderHome.setOrderDeliveryStatus("Cancelled");
			entityManager.merge(dbOrderHome);
			entityManager.flush();
			
			
			EntityTenantHomeOrders dbTenant = entityManager.find(EntityTenantHomeOrders.class, dbOrderHome.getTenantId());
			
        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/" + dbTenant.getTenantUrl() +  "?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
    		String username = tenantDbUsername;
    		String password = tenantDbPassword;
			
	        try(
		    		Connection connection = DriverManager.getConnection(url, username, password);			    		
		            Statement statement = connection.createStatement();		
	        ) { 
	            
	        /*    String getOrderData = "SELECT orders.*, payment_mode FROM orders LEFT JOIN "
	            		+ "(select payment_order_id, payment_mode, max(payment_id) AS max_payment_id  FROM customer_payments GROUP BY payment_order_id) p"
	            		+ " ON orders.order_id = p.payment_order_id WHERE order_id = " + dbOrder.getOrderNo(); */
	
	            
		        String updateOrderDeliveryStatus = "UPDATE orders SET order_delivery_status = \"Cancelled\" WHERE order_id =  " + dbOrderHome.getOrderNo(); 	            
	            statement.executeUpdate(updateOrderDeliveryStatus);	            
	            String updateBatchesDeliveryStatus = "UPDATE batches SET transaction_status = \"order-cancelled\" WHERE batch_order_id =  " + dbOrderHome.getOrderNo(); 	            	
	            statement.executeUpdate(updateBatchesDeliveryStatus);           
	       } 
	       catch (SQLException e) {
	          // e.printStackTrace();
	    	   throw e;
	       }
	       return true;
		}
		return false;
	}
	

	public ResponseOrdersHome findPendingOrders(Integer itemsPerPage, Integer startIndex) throws SQLException {
		
		ResponseOrdersHome responseOrders = new ResponseOrdersHome();

		Session currentSession = entityManager.unwrap(Session.class);
		
		Query countQuery = currentSession.createQuery("select count(orderId) from EntityOrderUserHome where "
								+ " orderDeliveryStatus=:orderDeliveryStatus");	
		
		countQuery.setParameter("orderDeliveryStatus", "Pending");
		
		Long countResults =  (Long) ((org.hibernate.query.Query) countQuery).uniqueResult();	
		
		responseOrders.setCountOfOrders(countResults);			
		
		Query theQuery = currentSession.createQuery("from EntityOrderHome WHERE orderDeliveryStatus=:deliveryStatus ORDER BY orderId DESC");	
		
		theQuery.setFirstResult(startIndex);
		theQuery.setMaxResults(itemsPerPage); 
		theQuery.setParameter("deliveryStatus", "Pending");
		
		List<EntityOrderHome> dbOrders = theQuery.getResultList();
		
		List<DtoOrderAndBatchesHome> dtoOrdersAndBatches = findBatchesFromOrders(dbOrders);
		
		responseOrders.setDtos(dtoOrdersAndBatches);
		
		return responseOrders;
		
		
	}

	public ResponseOrdersHome findOrdersOfCustomer(Integer customerId, Integer itemsPerPage, Integer startIndex) throws SQLException {
		ResponseOrdersHome responseOrders = new ResponseOrdersHome();
		
		Session currentSession = entityManager.unwrap(Session.class);
			
		Query countQuery = currentSession.createQuery("select count(orderId) from EntityOrderHome where "
								+ "customer.customerId =:customerId");	
		
		countQuery.setParameter("customerId", customerId);
		
		Long countResults =  (Long) ((org.hibernate.query.Query) countQuery).uniqueResult();	

		
		responseOrders.setCountOfOrders(countResults);			
		
		Query theQuery = currentSession.createQuery("from EntityOrderHome WHERE customer.customerId =:customerId ORDER BY orderId DESC");	
		
		theQuery.setFirstResult(startIndex);
		theQuery.setMaxResults(itemsPerPage); 
		theQuery.setParameter("customerId", customerId);
		
		List<EntityOrderHome> dbOrders = theQuery.getResultList();
		
		List<DtoOrderAndBatchesHome> dtoOrdersAndBatches = findBatchesFromOrders(dbOrders);
		
		responseOrders.setDtos(dtoOrdersAndBatches);
		
		return responseOrders;
		
	}

	public DtoOrderAndBatchesHome findOrderByOrderId(Long orderId) throws SQLException {
		
        DtoOrderAndBatchesHome dtoOrderAndBatches = new DtoOrderAndBatchesHome();
        DtoOrderHome dtoOrder = new DtoOrderHome();
        List<DtoBatchAndProductHome> batches = new ArrayList<>(); 
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		EntityOrderHome dbOrder = entityManager.find(EntityOrderHome.class, orderId);	
		
		Query queryTenant = entityManager.createQuery("from EntityTenantHomeOrders where tenantId =:tenantId");
		queryTenant.setParameter("tenantId", dbOrder.getTenantId());
		
		EntityTenantHomeOrders dbTenant = (EntityTenantHomeOrders) queryTenant.getSingleResult();	
		
    	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/" + dbTenant.getTenantUrl() +  "?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
		String username = tenantDbUsername;
		String password = tenantDbPassword;
		
        try(
	    		Connection connection = DriverManager.getConnection(url, username, password);			    		
	            Statement statement = connection.createStatement();		
        ) { 
	        
	        String getOrderData = getOrderDataString + dbOrder.getOrderNo();
            ResultSet resultSet = statement.executeQuery(getOrderData);
                       
            dtoOrder = findDtoOrderFromResultSet(resultSet, dbOrder, dbTenant);
            
            dtoOrderAndBatches.setOrder(dtoOrder);
            
            String getBatchesData = getBatchesDataString + dbOrder.getOrderNo();
            
            ResultSet resultSetBatches = statement.executeQuery(getBatchesData);

            batches = findBatchesFromResultSet(resultSetBatches);
            
            dtoOrderAndBatches.setBatches(batches);

            
        } catch (SQLException e) {
             //  e.printStackTrace();
        	throw e;
        }	       		
		
		return dtoOrderAndBatches;
	}

	public ResponseOrdersHome findOrdersOfTenant(Integer tenantId, Integer itemsPerPage, Integer startIndex) throws SQLException {
		ResponseOrdersHome responseOrders = new ResponseOrdersHome();
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query countQuery = currentSession.createQuery("select count(orderId) from EntityOrderHome where "
								+ "tenantId =:tenantId");	
		
		countQuery.setParameter("tenantId", tenantId);
		
		Long countResults =  (Long) ((org.hibernate.query.Query) countQuery).uniqueResult();	
		
		responseOrders.setCountOfOrders(countResults);			
		
		Query theQuery = currentSession.createQuery("from EntityOrderHome WHERE tenantId =:tenantId ORDER BY orderId DESC");	
		
		theQuery.setFirstResult(startIndex);
		theQuery.setMaxResults(itemsPerPage); 
		theQuery.setParameter("tenantId", tenantId);
		
		List<EntityOrderHome> dbOrders = theQuery.getResultList();
		
		Query queryTenant = entityManager.createQuery("from EntityTenantHomeOrders where tenantId =:tenantId");
		queryTenant.setParameter("tenantId", dbOrders.get(0).getTenantId());
		
		EntityTenantHomeOrders dbTenant = (EntityTenantHomeOrders) queryTenant.getSingleResult();
		

				
		List<DtoOrderAndBatchesHome> dtoOrdersAndBatches = new ArrayList<>();
		
 	   String url = "jdbc:mysql://" + tenantDbUrl + ":3306/" + dbTenant.getTenantUrl() +  "?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
 	   String username = tenantDbUsername;
	   String password = tenantDbPassword;

	   /*
       try { 
    	   
   			Connection connection = DriverManager.getConnection(url, username, password);
    	   
   			for(EntityOrderHome dbOrder : dbOrders) {
	    		
	            Statement statement = connection.createStatement();
	            
	            String getOrderData = getOrderDataString + dbOrder.getOrderNo();            

	            ResultSet resultSet = statement.executeQuery(getOrderData);
	            
	            DtoOrderAndBatchesHome dtoOrderAndBatches = new DtoOrderAndBatchesHome();
	            
	            DtoOrderHome dtoOrder = new DtoOrderHome();
	            
	            dtoOrder = findDtoOrderFromResultSet(resultSet, dbOrder, dbTenant);
	            
	            dtoOrderAndBatches.setOrder(dtoOrder);

	            List<DtoBatchAndProductHome> batches = new ArrayList<>();
	            
	            String getBatchesData = getBatchesDataString + dbOrder.getOrderNo();

	            ResultSet resultSetBatches = statement.executeQuery(getBatchesData);
	            
	            batches = findBatchesFromResultSet(resultSetBatches);
	            
	            dtoOrderAndBatches.setBatches(batches);
	            dtoOrdersAndBatches.add(dtoOrderAndBatches);	            
	            responseOrders.setDtos(dtoOrdersAndBatches);
   			}    
            
        } catch (SQLException e) {
              // e.printStackTrace();
        	throw e;
        }	
       */
       
       
       try (Connection connection = DriverManager.getConnection(url, username, password)) {
    	    for (EntityOrderHome dbOrder : dbOrders) {
    	        try (Statement statement = connection.createStatement()) {
    	            String getOrderData = getOrderDataString + dbOrder.getOrderNo();
    	            try (ResultSet resultSet = statement.executeQuery(getOrderData)) {
    	                DtoOrderAndBatchesHome dtoOrderAndBatches = new DtoOrderAndBatchesHome();
    	                DtoOrderHome dtoOrder = findDtoOrderFromResultSet(resultSet, dbOrder, dbTenant);
    	                dtoOrderAndBatches.setOrder(dtoOrder);

    	                List<DtoBatchAndProductHome> batches = new ArrayList<>();
    	                String getBatchesData = getBatchesDataString + dbOrder.getOrderNo();

    	                try (ResultSet resultSetBatches = statement.executeQuery(getBatchesData)) {
    	                    batches = findBatchesFromResultSet(resultSetBatches);
    	                }

    	                dtoOrderAndBatches.setBatches(batches);
    	                dtoOrdersAndBatches.add(dtoOrderAndBatches);
    	                responseOrders.setDtos(dtoOrdersAndBatches);
    	            }
    	        }
    	    }
    	} catch (SQLException e) {
    	    // Handle the exception or rethrow it if needed
    	    throw e;
    	}
		
		return responseOrders;
		
	}
	
	public List<DtoOrderAndBatchesHome> findOrdersAndBatchesInDatesRange(Date fromDate, Date toDate, String customerType) throws SQLException {
			
		Query theQuery = (Query) entityManager.createQuery("from EntityOrderHome where (dateTimeDelivered BETWEEN :fromDate AND :toDate) "
															+ "AND (orderDeliveryStatus=:delivered OR orderDeliveryStatus=:salesReturn "
															+ "OR orderDeliveryStatus=:completed) AND customer.customerType =:customerType ORDER BY orderId DESC");
		theQuery.setParameter("fromDate", fromDate);
		theQuery.setParameter("toDate", toDate);
		theQuery.setParameter("delivered", "Delivered");
		theQuery.setParameter("salesReturn", "Sales Return");
		theQuery.setParameter("completed", "Completed");
		theQuery.setParameter("customerType", customerType);
		
		List<EntityOrderHome> dbOrders = theQuery.getResultList();	
		
		Collections.reverse(dbOrders);
		
		List<DtoOrderAndBatchesHome> dtoOrdersAndBatches = findBatchesFromOrders(dbOrders);
			
		return dtoOrdersAndBatches;				
	}
	
	
	private List<DtoOrderAndBatchesHome> findBatchesFromOrders (List<EntityOrderHome> dbOrders) throws SQLException {
		
		List<DtoOrderAndBatchesHome> dtoOrdersAndBatches = new ArrayList<>();
		
		
		for(EntityOrderHome dbOrder : dbOrders) {
			
	//		EntityOrderHome or = entityManager.find(EntityOrderHome.class, dbOrder.getOrderId());
			
			Query queryTenant = entityManager.createQuery("from EntityTenantHomeOrders where tenantId =:tenantId");
			queryTenant.setParameter("tenantId", dbOrder.getTenantId());
			
			EntityTenantHomeOrders dbTenant = (EntityTenantHomeOrders) queryTenant.getSingleResult();	
			
        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/" + dbTenant.getTenantUrl() +  "?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
    		String username = tenantDbUsername;
    		String password = tenantDbPassword;
	/*		
	       try {       	

	    		Connection connection = DriverManager.getConnection(url, username, password);    		
	            Statement statement = connection.createStatement();		        
		        String getOrderData = getOrderDataString + dbOrder.getOrderNo(); 

	            ResultSet resultSet = statement.executeQuery(getOrderData);	            
	            DtoOrderAndBatchesHome dtoOrderAndBatches = new DtoOrderAndBatchesHome();	            
	            DtoOrderHome dtoOrder = new DtoOrderHome();
	            
	            dtoOrder = findDtoOrderFromResultSet(resultSet, dbOrder, dbTenant);	            
	            dtoOrderAndBatches.setOrder(dtoOrder);
	            List<DtoBatchAndProductHome> batches = new ArrayList<>();
	            
	   //         String getBatchesData = getBatchesDataString + dbOrder.getOrderNo();
	            
	            String getBatchesData = "SELECT * FROM batches WHERE batch_order_id = " + dbOrder.getOrderNo()  
	            						+ " AND ( transaction_status IS NULL OR  (transaction_status IS NOT NULL AND  transaction_status != 'batch-deleted')) "
	            						+ " ORDER BY  batch_id DESC LIMIT " + dtoOrder.getNumberOfBatches();          

	            ResultSet resultSetBatches = statement.executeQuery(getBatchesData);	            
	            batches = findBatchesFromResultSet(resultSetBatches);	            
	            Collections.reverse(batches);	            
	            dtoOrderAndBatches.setBatches(batches);
	            dtoOrdersAndBatches.add(dtoOrderAndBatches);	            
	            
	        } catch (SQLException e) {
	             //  e.printStackTrace();
	        	throw e;
	        }
	       */
    		
    		try (
    			    Connection connection = DriverManager.getConnection(url, username, password);
    			    Statement statement = connection.createStatement()
    			) {
    			    String getOrderData = getOrderDataString + dbOrder.getOrderNo();
    			    try (ResultSet resultSet = statement.executeQuery(getOrderData)) {
    			        DtoOrderAndBatchesHome dtoOrderAndBatches = new DtoOrderAndBatchesHome();
    			        DtoOrderHome dtoOrder = findDtoOrderFromResultSet(resultSet, dbOrder, dbTenant);
    			        dtoOrderAndBatches.setOrder(dtoOrder);

    			        List<DtoBatchAndProductHome> batches = new ArrayList<>();

    			        String getBatchesData = "SELECT * FROM batches WHERE batch_order_id = ? AND (transaction_status IS NULL OR (transaction_status IS NOT NULL AND transaction_status != 'batch-deleted')) ORDER BY batch_id DESC LIMIT ?";
    			        try (PreparedStatement preparedStatement = connection.prepareStatement(getBatchesData)) {
    			            preparedStatement.setFloat(1, dbOrder.getOrderNo());
    			            preparedStatement.setInt(2, dtoOrder.getNumberOfBatches());

    			            try (ResultSet resultSetBatches = preparedStatement.executeQuery()) {
    			                batches = findBatchesFromResultSet(resultSetBatches);
    			            }
    			        }

    			        Collections.reverse(batches);
    			        dtoOrderAndBatches.setBatches(batches);
    			        dtoOrdersAndBatches.add(dtoOrderAndBatches);
    			    }
    			} catch (SQLException e) {
    			    // Handle the exception or rethrow it if needed
    			    throw e;
    			}

		}
		
		return dtoOrdersAndBatches;
	
	}
	
	private List<DtoBatchAndProductHome> findBatchesFromResultSet (ResultSet resultSetBatches) throws SQLException{
		
		List<DtoBatchAndProductHome> batches = new ArrayList<>();
		
        while (resultSetBatches.next()) {
        	
        	DtoBatchAndProductHome batch = new DtoBatchAndProductHome();

        	batch.setBatchId(resultSetBatches.getInt("batch_id"));
        	batch.setBatchProductId(resultSetBatches.getInt("batch_product_id"));
        	batch.setBatchProductName(resultSetBatches.getString("batch_product_name"));
        	batch.setBatchUnit(resultSetBatches.getString("batch_unit"));
        	batch.setBatchBrandName(resultSetBatches.getString("batch_brand_name"));
        	batch.setBatchNo(resultSetBatches.getString("batch_no"));
        	batch.setBatchPurSaleBool(resultSetBatches.getInt("batch_pur_sale_bool"));
        	batch.setQuantity(resultSetBatches.getFloat("quantity"));
        	batch.setMrp(resultSetBatches.getFloat("batch_mrp"));
        	batch.setSellingPrice(resultSetBatches.getInt("selling_price"));
        	batch.setBatchPurchasePrice(resultSetBatches.getFloat("batch_purchase_price"));
        	batch.setPpIncludesGST(resultSetBatches.getBoolean("pp_includes_gst"));
        	batch.setBatchGST(resultSetBatches.getInt("batch_gst"));
        	batch.setExpiryDate(resultSetBatches.getDate("expiry_date"));
        	batch.setBatchOrderId(resultSetBatches.getInt("batch_order_id"));
        	batch.setTransactionStatus(resultSetBatches.getString("transaction_status"));
        	
        	batches.add(batch);	            	
        }
		
		return batches;
		
	}
	
	
	private DtoOrderHome findDtoOrderFromResultSet (ResultSet resultSet, EntityOrderHome dbOrder, EntityTenantHomeOrders dbTenant ) throws SQLException {
		
		DtoOrderHome dtoOrder = new DtoOrderHome();
		
        while (resultSet.next()) {
        	
        	dtoOrder.setOrderHomeId(dbOrder.getOrderId());	            	
            dtoOrder.setOrderId(resultSet.getLong("order_id"));
            dtoOrder.setSalesReturnId(resultSet.getLong("sales_return_id")); 
            dtoOrder.setNumberOfBatches(resultSet.getInt("number_of_batches"));
            dtoOrder.setOrderDeliveryStatus(resultSet.getString("order_delivery_status")); 
            dtoOrder.setOrderSourceType(resultSet.getString("order_source_type"));
            dtoOrder.setSubTotal(resultSet.getInt("sub_total"));
            dtoOrder.setPendingPayment(resultSet.getInt("pending_payment"));
            dtoOrder.setShippingCharges(resultSet.getInt("shipping_charges")); 
            dtoOrder.setDateTimeCreated(resultSet.getTimestamp("date_time_created"));
            dtoOrder.setDateCreated(resultSet.getDate("date_created")); 
            dtoOrder.setDateTimeDelivered(resultSet.getDate("date_time_delivered"));
            dtoOrder.setTenantName(dbTenant.getTenantName());
            dtoOrder.setTenantId(dbTenant.getTenantId());
      //      dtoOrders.setShippingAddressId(resultSet.getInt("shipping_address_id"));
            dtoOrder.setComments(resultSet.getString("comments"));  
            EntityCustomerHomeOrders customer = entityManager.find(EntityCustomerHomeOrders.class, dbOrder.getCustomer().getCustomerId());	                
            dtoOrder.setCustomer(customer);
            dtoOrder.setPaymentMode(resultSet.getString("payment_mode"));
        }
        
        return dtoOrder;
		
	}
	
	
	
	

}
