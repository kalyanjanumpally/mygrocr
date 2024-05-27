package springboot.adminTenant.orders;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import springboot.properties.TenantDbProperties;


@Repository
public class DAOOrders {

	private EntityManager entityManager;
	
	private String tenantDbUrl;
	private String tenantDbUsername;
	private String tenantDbPassword;

	
	//set up constructor injection	
	public DAOOrders() {	
	}
	
	@Autowired
	public DAOOrders(EntityManager theEntityManager, TenantDbProperties tenantDbProperties) {	
		this.entityManager = theEntityManager;
		this.tenantDbUrl = tenantDbProperties.getTenantsDbUrl();
		this.tenantDbUsername = tenantDbProperties.getTenantDbUsername();
		this.tenantDbPassword = tenantDbProperties.getTenantDbPassword();
	}


	public EntityOrder save(EntityOrder theOrder) {			
	//	entityManager.getTransaction().begin();
		EntityOrder dbOrder =  entityManager.merge(theOrder);
	//	entityManager.getTransaction().commit();
	//	entityManager.close();
		return dbOrder;
		
	}
	

	public EntityOrder saveSalesReturn(EntityOrder order, String orderSourceType, String tenantUrl) throws SQLException {		
		order.setOrderDeliveryStatus("Sales Return");
		
		EntityOrder dbOrder = entityManager.merge(order);
		
		if(orderSourceType.equals("Online")) {
			
	       try {       	
	        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/home_company?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
	    		String username = tenantDbUsername;
	    		String password = tenantDbPassword;
	    		Connection connection = DriverManager.getConnection(url, username, password);
	    		
	            Statement statement = connection.createStatement();
	
		        String getTenantId = "SELECT tenant_id from tenants WHERE tenant_url = \"" + tenantUrl + "\" LIMIT 1"; 	            
	
	            ResultSet resultSet = statement.executeQuery(getTenantId);
	            
	            Integer tenantId = 0;
	            while (resultSet.next()) {
	            	tenantId = resultSet.getInt("tenant_id");
	            }
	            
	    		java.util.Date date = new java.util.Date();
	            
	    		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	            String dateTimeDeliveredString = dateFormat.format(date);	            
	            
	            String saveReturnOrderInHome =  "INSERT INTO orders (tenant_id, order_return_no, order_no, customer_id, order_delivery_status, date_time_delivered) "
	            		+ "VALUES ( " 
	            		+ tenantId + ", " 
	            		+ order.getSalesReturnId()  + ", " 
	            		+ dbOrder.getOrderId() + ", " 
	            		+ dbOrder.getCustomer().getCustomerId() + ", " 
	            		+ "\"" + dbOrder.getOrderDeliveryStatus() + "\", "
	            		+ "\"" +  dateTimeDeliveredString  + "\"" 
	            		+ ")";
	            
	            statement.executeUpdate(saveReturnOrderInHome);      
	       } 
	       catch (SQLException e) {
	         //  e.printStackTrace();
	    	   throw e;
	       }			
				
		}
		
		return dbOrder;
		
	} 
	

	public EntityOrder findById(Long orderId) { //this is generic method for all order source types
		
		Session currentSession = entityManager.unwrap(Session.class);		
		return currentSession.get(EntityOrder.class, orderId); 			
	}
	
	
	public EntityOrder findOrderByOrderId(String orderSourceType, Long orderId) {
		
		Session currentSession = entityManager.unwrap(Session.class);		
		EntityOrder dbOrder = currentSession.get(EntityOrder.class, orderId);
		
		if(dbOrder != null && dbOrder.getOrderSourceType().equals(orderSourceType)) {
			return dbOrder;
		}		
		return null;
	}


	public List<EntityOrder> findAllOrders(String orderSourceType, Integer itemsPerPage, Integer startIndex) {		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query<EntityOrder> theQuery =
				  currentSession.createQuery("from EntityOrder where orderSourceType=:orderSourceType ORDER BY orderId DESC", EntityOrder.class);
		theQuery.setParameter("orderSourceType",orderSourceType);	
		
		theQuery.setFirstResult(startIndex);
		theQuery.setMaxResults(itemsPerPage); 
		
		
		//execute the query and get result list 
		List<EntityOrder> dbOrders = theQuery.getResultList();
				  
	    //return result 
		return dbOrders; 
	}
	

	public List<EntityOrder> findAllOrdersSalesOnlyOnlinePhone(Integer itemsPerPage, Integer startIndex) {
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query<EntityOrder> theQuery =
				  currentSession.createQuery("from EntityOrder where (orderSourceType=:orderSourceType1 OR orderSourceType=:orderSourceType2) AND orderDeliveryStatus<>:deliveryStatus ORDER BY orderId DESC", EntityOrder.class);	
		theQuery.setParameter("orderSourceType1","Online");
		theQuery.setParameter("orderSourceType2","Phone");
		theQuery.setParameter("deliveryStatus", "Sales Return");	
		theQuery.setFirstResult(startIndex);
		theQuery.setMaxResults(itemsPerPage); 
		
		
		//execute the query and get result list 
		List<EntityOrder> dbOrders = theQuery.getResultList();
				  
	    //return result 
		return dbOrders;
	}
	

	public List<EntityOrder> findOrdersOfCustomer(String orderSourceType, Integer customerId, Integer itemsPerPage, Integer startIndex) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query theQuery =
				  currentSession.createQuery("select o from EntityOrder o JOIN o.customer c where o.orderSourceType =:orderSourceType AND "
				  		+ "c.customerId =:customerId");
		theQuery.setParameter("orderSourceType",orderSourceType);
		theQuery.setParameter("customerId",customerId);
		
		theQuery.setFirstResult(startIndex);
		theQuery.setMaxResults(itemsPerPage); 		
		
		//execute the query and get result list 
		List<EntityOrder> dbOrders = theQuery.getResultList();
				  
	    //return result 
		return dbOrders; 
	}

	

	public List<EntityOrder> findAllPendingOrders(String orderSourceType) {		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query<EntityOrder> theQuery =
				  currentSession.createQuery("from EntityOrder where orderSourceType=:orderSourceType AND orderDeliveryStatus=:orderDeliveryStatus", EntityOrder.class);
		theQuery.setParameter("orderSourceType",orderSourceType);
		theQuery.setParameter("orderDeliveryStatus","Pending");
		
		//execute the query and get result list 
		List<EntityOrder> dbOrders = theQuery.getResultList();
				  
	    //return result 
		return dbOrders; 
	}
	

	public List<EntityOrder> findAllOrders() {
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query<EntityOrder> theQuery =
				  currentSession.createQuery("from EntityOrder", EntityOrder.class);	  
		//execute the query and get result list 
		List<EntityOrder> dbOrders = theQuery.getResultList();
				  
	    //return result 
		return dbOrders;
	}
	
	
	public List<EntityOrder> getOrderSalesReturn(Long orderId) {
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query theQuery =
				  currentSession.createQuery("from EntityOrder where orderId=:orderId OR salesReturnId=:salesReturnId");	  
		theQuery.setParameter("orderId", orderId);
		theQuery.setParameter("salesReturnId", orderId);
		
		//execute the query and get result list 
		List<EntityOrder> dbOrders = theQuery.getResultList();
		
		for(EntityOrder order : dbOrders) {
			if(order.getOrderId().equals(orderId)) {
				if(order.getOrderDeliveryStatus() != null) {
					if(order.getOrderDeliveryStatus().equals("Returned") || order.getOrderDeliveryStatus().equals("Cancelled")) {
						return null;
					}
				}
			}
		}
				  
	    //return result 
		return dbOrders;
	}
	

	public Long countOfAllOrders(String orderSourceType) {		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query countQuery =
				  currentSession.createQuery("select count(o.orderId) from EntityOrder o where o.orderSourceType=:orderSourceType");
		countQuery.setParameter("orderSourceType",orderSourceType);	 
		
		Long countResults =  (Long) countQuery.uniqueResult();
	
				  
	    //return result 
		return countResults; 
	}
	

	public Long countOfAllOrdersSalesOnlyOnlinePhone() {
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query countQuery = currentSession.createQuery("select count(o.orderId) from EntityOrder o ");	 
		
		Long countResults =  (Long) countQuery.uniqueResult();
		
	    //return result 
		return countResults; 
	}
	
	
	public Long countOfAllOrdersOfCustomer(Integer customerId, String orderSourceType) {
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query countQuery =
				  currentSession.createQuery("select count(o.orderId) from EntityOrder o JOIN o.customer c where o.orderSourceType=:orderSourceType"
				  		+ " AND c.customerId =:customerId");
		countQuery.setParameter("orderSourceType",orderSourceType);	
		countQuery.setParameter("customerId",customerId);	
		
		Long countResults =  (Long) countQuery.uniqueResult();	
				  
	    //return result 
		return countResults; 
	}


	public EntityOrder cancelOrder(Long orderId, String tenantUrl) throws SQLException {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		EntityOrder dbOrder = currentSession.get(EntityOrder.class, orderId); 				
		dbOrder.setOrderDeliveryStatus("Cancelled");
		dbOrder.setPendingPayment(dbOrder.getPendingPayment()-dbOrder.getShippingCharges()-dbOrder.getSubTotal());
		currentSession.saveOrUpdate(dbOrder);	
		currentSession.flush();
		
		if(dbOrder.getOrderSourceType().equals("Online")) {				
	       try {       	
	        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/home_company?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
	    		String username = tenantDbUsername;
	    		String password = tenantDbPassword;
	    		Connection connection = DriverManager.getConnection(url, username, password);
	    		
	            Statement statement = connection.createStatement();
	
		        String getTenantId = "SELECT tenant_id from tenants WHERE tenant_url = \"" + tenantUrl + "\" LIMIT 1"; 	            
	
	            ResultSet resultSet = statement.executeQuery(getTenantId);
	            
	            Integer tenantId = 0;
	            while (resultSet.next()) {
	            	tenantId = resultSet.getInt("tenant_id");
	            }
	            
	            String updateCancelledOrderStatus = "UPDATE orders SET order_delivery_status = \"Cancelled\" WHERE tenant_id = " + tenantId + " AND order_no = " + orderId; 	            
	
	            statement.executeUpdate(updateCancelledOrderStatus);         
	       } 
	       catch (SQLException e) {
	         //  e.printStackTrace();
	    	   throw e;
	       }
		}
		return dbOrder;
	}
	

	public EntityOrder deliverOrder(Long orderId, String tenantUrl) {
		Session currentSession = entityManager.unwrap(Session.class);
		
	//	long millis = System.currentTimeMillis(); 
	//	long timeCorrectionISTtoGMT = (long) (5.5*60*60*1000);
	//	java.sql.Date dateTimeDelivered = new java.sql.Date(millis + timeCorrectionISTtoGMT);		
        
		java.util.Date date = new java.util.Date();
        
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String dateTimeDeliveredString = dateFormat.format(date);
		
		EntityOrder dbOrder = currentSession.get(EntityOrder.class, orderId); 				
		dbOrder.setOrderDeliveryStatus("Delivered");
		dbOrder.setDateTimeDelivered(date);
		currentSession.saveOrUpdate(dbOrder);
		currentSession.flush();
		
		if(dbOrder.getOrderSourceType().equals("Online")) {	
		
		      try {       	
		        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/home_company?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
		    		String username = tenantDbUsername;
		    		String password = tenantDbPassword;
		    		Connection connection = DriverManager.getConnection(url, username, password);
		    		
		            Statement statement = connection.createStatement();
		            
			        String getTenantId = "SELECT tenant_id from tenants WHERE tenant_url = \"" + tenantUrl + "\" LIMIT 1"; 	            
			       
		            ResultSet resultSet = statement.executeQuery(getTenantId);
		            
		            Integer tenantId = 0;
		            while (resultSet.next()) {
		            	tenantId = resultSet.getInt("tenant_id");
		            }
		            
		            String updateDeliveredOrderStatus = "UPDATE orders SET order_delivery_status = \"Delivered\", date_time_delivered = \"" + dateTimeDeliveredString + "\" WHERE tenant_id = " + tenantId + " AND order_no = " + orderId; 	            
		            statement.executeUpdate(updateDeliveredOrderStatus);         
		       } 
		       catch (SQLException e) {
		           e.printStackTrace();
		       }
		}
		return dbOrder;
	}
	
	
	
	public void acceptOrder(Long orderId, String tenantUrl) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		EntityOrder dbOrder = currentSession.get(EntityOrder.class, orderId); 				
		dbOrder.setOrderDeliveryStatus("Pending");
		currentSession.saveOrUpdate(dbOrder);
		currentSession.flush();
		
	      try {       	
	        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/home_company?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
	    		String username = tenantDbUsername;
	    		String password = tenantDbPassword;
	    		Connection connection = DriverManager.getConnection(url, username, password);
	    		
	            Statement statement = connection.createStatement();
	            
		        String getTenantId = "SELECT tenant_id from tenants WHERE tenant_url = \"" + tenantUrl + "\" LIMIT 1"; 	            
		       
	            ResultSet resultSet = statement.executeQuery(getTenantId);
	            
	            Integer tenantId = 0;
	            while (resultSet.next()) {
	            	tenantId = resultSet.getInt("tenant_id");
	            }
	            
	            String updateAcceptOrderStatus = "UPDATE orders SET order_delivery_status = \"Pending\" WHERE tenant_id = " + tenantId + " AND order_no = " + orderId; 	            
	            statement.executeUpdate(updateAcceptOrderStatus);         
	       } 
	       catch (SQLException e) {
	           e.printStackTrace();
	       }

	}
	
	
	
	
	public EntityOrder completeOrder(Long orderId) {
		Session currentSession = entityManager.unwrap(Session.class);
		
		long millis = System.currentTimeMillis(); 
		java.sql.Date dateTimeDelivered = new java.sql.Date(millis);
		
		EntityOrder dbOrder = currentSession.get(EntityOrder.class, orderId); 				
		dbOrder.setOrderDeliveryStatus("Completed");	
		dbOrder.setDateTimeDelivered(dateTimeDelivered);
		currentSession.saveOrUpdate(dbOrder);
		currentSession.flush();
		
		return dbOrder;
	}
	

	public EntityOrder returnOrder(Long orderId, String tenantUrl) throws SQLException {
		Session currentSession = entityManager.unwrap(Session.class);
		
		EntityOrder dbOrder = currentSession.get(EntityOrder.class, orderId); 				
		dbOrder.setOrderDeliveryStatus("Returned");	
		dbOrder.setPendingPayment(dbOrder.getPendingPayment()- (dbOrder.getSubTotal() + dbOrder.getShippingCharges()));	
		currentSession.saveOrUpdate(dbOrder);
		currentSession.flush();
		
		
		if(dbOrder.getOrderSourceType().equals("Online")) {	
			
		      try {       	
		        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/home_company?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
		    		String username = tenantDbUsername;
		    		String password = tenantDbPassword;
		    		Connection connection = DriverManager.getConnection(url, username, password);
		    		
		            Statement statement = connection.createStatement();
		            
			        String getTenantId = "SELECT tenant_id from tenants WHERE tenant_url = \"" + tenantUrl + "\" LIMIT 1"; 	            
			       
		            ResultSet resultSet = statement.executeQuery(getTenantId);
		            
		            Integer tenantId = 0;
		            while (resultSet.next()) {
		            	tenantId = resultSet.getInt("tenant_id");
		            }
		            
		            String updateCancelledOrderStatus = "UPDATE orders SET order_delivery_status = \"Returned\" WHERE tenant_id = " + tenantId + " AND order_no = " + orderId; 	            
		            statement.executeUpdate(updateCancelledOrderStatus);         
		       } 
		       catch (SQLException e) {
		           //e.printStackTrace();
		    	   throw e;
		       }
		} 
		return dbOrder;
	}


	public List<EntityOrder> getOrdersWithPendingPayments(String customerType) {
	//	Session currentSession = entityManager.unwrap(Session.class);		
		 
		Query theQuery = (Query) entityManager.createQuery("from EntityOrder o where abs(pendingPayment) >:pendingPayment AND o.customer.customerType=:customerType");
		theQuery.setParameter("pendingPayment", (float)0.5);
		theQuery.setParameter("customerType", customerType);
		 	 
		List<EntityOrder> dbOrders = theQuery.getResultList();
		
		List<Long> orderIds = new ArrayList<Long>();
		
		for(EntityOrder order : dbOrders) {		
			orderIds.add(order.getOrderId());	
		}
					
		Query theQuery2 = (Query) entityManager.createQuery("from EntityOrder where salesReturnId in (:salesReturnIds)");
		theQuery2.setParameter("salesReturnIds", orderIds);
		
		List<EntityOrder> dbOrders2 = theQuery2.getResultList();
		
		for(EntityOrder order2 : dbOrders2) {	
			dbOrders.add(order2);	
		}
		 return dbOrders;
	} 


	public void updateOrder(EntityOrder order) {
		
	//	Session currentSession = entityManager.unwrap(Session.class);
		EntityOrder dbOrder = entityManager.find(EntityOrder.class, order.getOrderId()); 
		
		List<EntityCustomerPaymentsOrders> payments = dbOrder.getPayments();
		
		EntityCustomerPaymentsOrders newPaymentEntry = new EntityCustomerPaymentsOrders();
		
		long millis = System.currentTimeMillis(); 
		java.sql.Date todayDate = new java.sql.Date(millis);
		
		newPaymentEntry.setPaymentId((long) 0);
		newPaymentEntry.setPaymentMode("Sale On Credit");
		newPaymentEntry.setAmount(order.getSubTotal() + order.getShippingCharges() - dbOrder.getSubTotal() - dbOrder.getShippingCharges() );
		newPaymentEntry.setPaymentDate(todayDate);
		newPaymentEntry.setOrder(dbOrder);
		payments.add(newPaymentEntry);
		
		dbOrder.setPendingPayment(dbOrder.getPendingPayment() + order.getSubTotal() + order.getShippingCharges() - dbOrder.getSubTotal() - dbOrder.getShippingCharges() );				
		dbOrder.setSubTotal(order.getSubTotal());
		dbOrder.setShippingCharges(order.getShippingCharges());
		dbOrder.setNumberOfBatches(order.getNumberOfBatches());
		entityManager.merge(dbOrder);
		entityManager.flush();
	}
	

	public List<EntityOrder> findOrdersInDatesRange(Date fromDate, Date toDate, String customerType) {
		
		System.out.println("fromDate: " + fromDate);
		System.out.println("toDate: " + toDate);
		
		
		Query theQuery = (Query) entityManager.createQuery("from EntityOrder where (dateTimeDelivered BETWEEN :fromDate AND :toDate) "
															+ "AND (orderDeliveryStatus=:delivered OR orderDeliveryStatus=:salesReturn "
															+ "OR orderDeliveryStatus=:completed) AND customer.customerType =:customerType ORDER BY orderId DESC");
		theQuery.setParameter("fromDate", fromDate);
		theQuery.setParameter("toDate", toDate);
		theQuery.setParameter("delivered", "Delivered");
		theQuery.setParameter("salesReturn", "Sales Return");
		theQuery.setParameter("completed", "Completed");
		theQuery.setParameter("customerType", customerType);
		
		List<EntityOrder> dbOrders = theQuery.getResultList();	
		
		Collections.reverse(dbOrders);
		
		//set previous days walk-in orders as delivered.
		for(EntityOrder order : dbOrders){			
			
			if(order.getOrderSourceType() != null) {
				if(order.getOrderSourceType().equals("Walk-in")) {				
					if(order.getOrderDeliveryStatus() == null || 
							(order.getOrderDeliveryStatus() !=null && !order.getOrderDeliveryStatus().equals("Completed"))) {
					
						Date orderCreationDate = order.getDateTimeCreated();					
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");				
						String orderCreationDateString = dateFormat.format(orderCreationDate);
						String todayDateString = dateFormat.format(new Date());

						if(todayDateString.compareTo(orderCreationDateString) >= 1) {
							
							java.sql.Date orderCreationDateInSql = new java.sql.Date(orderCreationDate.getTime());
							
							order.setOrderDeliveryStatus("Completed");	
							order.setDateTimeDelivered(orderCreationDateInSql);
							entityManager.merge(order);					
						}
					}
				}	
			}			
		}	
		return dbOrders;				
	}


	public List<Object[]> findProductsSalesInDatesRange(Date fromDate, Date toDate, String customerType) {
		
		//first find only orders 
		
		List<EntityOrder> dbOrders = findOrdersInDatesRange(fromDate, toDate, customerType);
		
		Integer totalEntries = 0;
		
		for(EntityOrder order : dbOrders) {			
			totalEntries = totalEntries + order.getNumberOfBatches();
		}
		
		Query theQuery = (Query) entityManager.createQuery("SELECT  o.orderId, o.orderDeliveryStatus, b.batchId, b.batchProductId, b.batchVariantId, "
				+ " b.batchProductName, b.batchUnit, b.batchBrandName, b.quantity, b.mrp, "
				+ "p.gst, p.hsnCode FROM EntityOrder o LEFT OUTER JOIN EntityBatchesOrders b ON o.orderId = b.batchOrderId "
				+ "LEFT OUTER JOIN EntityProductOrders p ON b.batchProductId = p.productId "
				+ "WHERE (o.dateTimeDelivered BETWEEN :fromDate AND :toDate) AND  "
				+ " (o.orderDeliveryStatus=:delivered OR o.orderDeliveryStatus=:salesReturn "
				+ "OR o.orderDeliveryStatus=:completed) AND o.customer.customerType=:customerType "
				+ "AND (b.transactionStatus !=:batchDeleted ) "
				+ " ORDER BY o.orderId DESC ");
		
		theQuery.setParameter("fromDate", fromDate);
		theQuery.setParameter("toDate", toDate);
		theQuery.setParameter("delivered", "Delivered");
		theQuery.setParameter("salesReturn", "Sales Return");
		theQuery.setParameter("completed", "Completed");
		theQuery.setParameter("customerType", customerType);
		theQuery.setParameter("batchDeleted", "batch_deleted");
		theQuery.setMaxResults(totalEntries);
		
		Collections.reverse(dbOrders);

		return theQuery.list();	
	}


}
