package springboot.adminTenant.orders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceOrders {

	private DAOOrders dAOOrders;
	private DAOBatchesOrders dAOBatchesOrders;
	private DAOCustomerPayments dAOCustomerPayments;
	
	@Autowired
	public ServiceOrders(DAOOrders theDAOOrders, DAOBatchesOrders theDAOBatchesOrders, DAOCustomerPayments theDAOCustomerPayments) {		
		dAOOrders = theDAOOrders;
		dAOBatchesOrders = theDAOBatchesOrders;
		dAOCustomerPayments = theDAOCustomerPayments;
	}

	
	@Transactional
	public void save(DtoOrders dtoOrders) {
		EntityOrder order = dtoOrders.getOrder();
		order.setOrderId((long) 0);		
		
	    long millis = System.currentTimeMillis();  
		java.sql.Date todayDate = new java.sql.Date(millis); 
			
	/*	order.setDateCreated(todayDate); */
		
		List<EntityCustomerPaymentsOrders> payments =  order.getPayments();
		
		for(EntityCustomerPaymentsOrders payment : payments) {
			payment.setPaymentDate(todayDate);
			payment.setPaymentId((long) 0);
			payment.setOrder(order);
		}
		
		EntityOrder dbOrder = dAOOrders.save(order);
		
	/*	List<DtoBatchAndProductVariant> batches = dtoOrders.getBatches();
			
		for(DtoBatchAndProductVariant batch : batches) {
			batch.setBatchId(0);
			batch.setBatchOrderId(dbOrder.getOrderId()); 
			dAOBatchesOrders.save(batch);
		}
		*/
		
		List<EntityBatchesOrders> batches = dtoOrders.getBatches();
		
		for(EntityBatchesOrders batch : batches) {
			batch.setBatchId((long) 0);
			batch.setBatchOrderId(dbOrder.getOrderId()); 
			dAOBatchesOrders.save(batch);
		}
		
		
	}
	
	
	
	@Transactional
	public void saveSalesReturn(DtoOrders dto, String tenantUrl) throws SQLException {
			
	    long millis = System.currentTimeMillis();  
		Date todayDate = new Date(millis);
		
		EntityOrder order = dto.getOrder();
		order.setOrderId((long) 0);	
		
		List<EntityCustomerPaymentsOrders> returnPayments =  order.getPayments();
		
		float amountReturned = 0;
		
		for(EntityCustomerPaymentsOrders payment : returnPayments) {
			payment.setPaymentDate(todayDate);
			payment.setPaymentId((long) 0);
			payment.setOrder(order);
			
			if(!(payment.getPaymentMode().equals("Return On Credit") || payment.getPaymentMode().equals("Sale On Credit" ))) {
				amountReturned = amountReturned + payment.getAmount();
			}
		}
		
/*		order.setDateCreated(todayDate); */
		order.setDateTimeDelivered(todayDate);
		
		//Retrive previous returns entries from db for updating payment status-
		EntityOrder dbOrder = dAOOrders.findById(order.getSalesReturnId());
		
		String orderSourceType = dbOrder.getOrderSourceType();
		
		dbOrder.setPendingPayment(dbOrder.getPendingPayment() - order.getSubTotal() + amountReturned);

		EntityOrder dbUpdatedOrder = dAOOrders.save(dbOrder);
		EntityOrder dbReturnsOrder = dAOOrders.saveSalesReturn(order, orderSourceType, tenantUrl);
		
		/*
		List<DtoBatchAndProductVariant> batches = dto.getBatches();
		
		for(DtoBatchAndProductVariant batch : batches) {	
			batch.setBatchOrderId(dbReturnsOrder.getOrderId()); 
			dAOBatchesOrders.saveSalesReturn(batch);
		}
		*/
		
		List<EntityBatchesOrders> batches = dto.getBatches();
		
		for(EntityBatchesOrders batch : batches) {	
			batch.setBatchOrderId(dbReturnsOrder.getOrderId()); 
			batch.setBatchId((long) 0);
			dAOBatchesOrders.saveSalesReturn(batch);
		}
		
	}

	
	
	@Transactional
	public void saveCustomerPayments(List<DtoPayment> dtoPayments) {
		
	    long millis = System.currentTimeMillis();  
		Date todayDate = new Date(millis);
		
		for(DtoPayment dtoPayment : dtoPayments) {
			
			EntityOrder dbOrder = dAOOrders.findById(dtoPayment.getOrderId());
			dbOrder.setPendingPayment(dbOrder.getPendingPayment() - dtoPayment.getAmount());	
			List<EntityCustomerPaymentsOrders> dbPayments = dbOrder.getPayments();

			EntityCustomerPaymentsOrders payment = new EntityCustomerPaymentsOrders();
			
			payment.setAmount(dtoPayment.getAmount());
			payment.setPaymentMode(dtoPayment.getPaymentMode());
			payment.setPaymentDate(todayDate);
			payment.setOrder(dbOrder);	
			payment.setPaymentId((long) 0);
			
			dbPayments.add(payment);			
			dAOOrders.save(dbOrder);
			
			System.out.println("Order Id" + dbOrder.getOrderId() + ", amt: " +dtoPayment.getAmount() );
		}		
	}
	
	
	@Transactional
	public ResponseOrders findAllOrders(String orderSourceType, Integer itemsPerPage, Integer startIndex) {
		
		ResponseOrders responseOrders = new ResponseOrders();
		
		List<DtoOrders> dtoOrders = new ArrayList<DtoOrders>();	
		List<EntityOrder> orders = dAOOrders.findAllOrders(orderSourceType, itemsPerPage, startIndex);
			
		for(EntityOrder order : orders) {
			
			List<EntityBatchesOrders> dbBatches = dAOBatchesOrders.findBatches(order.getOrderId(), order.getNumberOfBatches());	
			DtoOrders dtoOrder = new DtoOrders();
			dtoOrder.setOrder(order);
			
			dtoOrder.setBatches(dbBatches);			
			dtoOrders.add(dtoOrder);
		}
		
		if(startIndex == 0) {
			Long countResults = dAOOrders.countOfAllOrders(orderSourceType);
			responseOrders.setCountOfOrders(countResults);
		}	
		
		responseOrders.setDtos(dtoOrders);
		
		return responseOrders;
	}
	
	
	
	public ResponseOrders getOrdersOfCustomer(String orderSourceType, Integer customerId, Integer itemsPerPage, Integer startIndex) {
		
		ResponseOrders responseOrders = new ResponseOrders();
		
		List<DtoOrders> dtoOrders = new ArrayList<DtoOrders>();	
		List<EntityOrder> orders = dAOOrders.findOrdersOfCustomer(orderSourceType, customerId, itemsPerPage, startIndex);
			
		for(EntityOrder order : orders) {
			
			//List<DtoBatchAndProductVariant> dbBatches = dAOBatchesOrders.findBatches(order.getOrderId(), order.getNumberOfBatches());	
			List<EntityBatchesOrders> dbBatches = dAOBatchesOrders.findBatches(order.getOrderId(), order.getNumberOfBatches());	
			DtoOrders dtoOrder = new DtoOrders();
			dtoOrder.setOrder(order);
			
			dtoOrder.setBatches(dbBatches);			
			dtoOrders.add(dtoOrder);
		}
		
		if(startIndex == 0) {
			Long countResults = dAOOrders.countOfAllOrdersOfCustomer(customerId, orderSourceType);
			responseOrders.setCountOfOrders(countResults);
		}	
		
		responseOrders.setDtos(dtoOrders);
		
		return responseOrders;
	}
	
	
	
	@Transactional
	public List<DtoOrders> findAllPendingOrders(String orderSourceType) {
		
		List<DtoOrders> dtoOrders = new ArrayList<DtoOrders>();	
		List<EntityOrder> orders = dAOOrders.findAllPendingOrders(orderSourceType);
			
		for(EntityOrder order : orders) {
			
			//List<DtoBatchAndProductVariant> dbBatches = dAOBatchesOrders.findBatches(order.getOrderId(), order.getNumberOfBatches());	
			List<EntityBatchesOrders> dbBatches = dAOBatchesOrders.findBatches(order.getOrderId(), order.getNumberOfBatches());	
			DtoOrders dtoOrder = new DtoOrders();
			dtoOrder.setOrder(order);
			
			dtoOrder.setBatches(dbBatches);			
			dtoOrders.add(dtoOrder);
		}			
		return dtoOrders;
	}
	
	
	
	@Transactional
	public List<DtoOrders> findAllOrders() {
		
		List<DtoOrders> dtoOrders = new ArrayList<DtoOrders>();		
		List<EntityOrder> orders = dAOOrders.findAllOrders();
		
		for(EntityOrder order : orders) {
						
			//List<DtoBatchAndProductVariant> dbBatches = dAOBatchesOrders.findBatches(order.getOrderId(), order.getNumberOfBatches());
			List<EntityBatchesOrders> dbBatches = dAOBatchesOrders.findBatches(order.getOrderId(), order.getNumberOfBatches());
			
			DtoOrders dtoOrder = new DtoOrders();
			dtoOrder.setOrder(order);
			dtoOrder.setBatches(dbBatches);			
			dtoOrders.add(dtoOrder);
		}

		return dtoOrders;
	}
	

	
	@Transactional
	public void cancelOrder(Long orderId, String tenantUrl) throws SQLException {
		EntityOrder dbOrder = dAOOrders.cancelOrder(orderId, tenantUrl);
		dAOBatchesOrders.cancelOrder(orderId, dbOrder.getNumberOfBatches());	
	}
	
	
	@Transactional
	public void deliverOrder(Long orderId, String tenantUrl) {
		EntityOrder dbOrder = dAOOrders.deliverOrder(orderId, tenantUrl);	
		dAOBatchesOrders.deliverOrder(orderId, dbOrder.getNumberOfBatches());	
	}
	
	@Transactional
	public void acceptOrder(Long orderId, String tenantUrl) {
		dAOOrders.acceptOrder(orderId, tenantUrl);		
	}
	
	
	@Transactional
	public void completeOrder(Long orderId) {
		EntityOrder dbOrder = dAOOrders.completeOrder(orderId);	
		dAOBatchesOrders.completeOrder(orderId, dbOrder.getNumberOfBatches());			
	}
	
	
	@Transactional
	public void returnOrder(Long orderId, String tenantUrl) throws SQLException {
		EntityOrder dbOrder = dAOOrders.returnOrder(orderId, tenantUrl);	
		dAOBatchesOrders.returnOrder(orderId, dbOrder.getNumberOfBatches());	
	
	}
	

	
	@Transactional
	public DtoOrders findById(Long orderId) {
		
		EntityOrder dbOrder = dAOOrders.findById(orderId);
		
		//List<DtoBatchAndProductVariant> dbBatches = dAOBatchesOrders.findBatches(orderId, dbOrder.getNumberOfBatches());
		List<EntityBatchesOrders> dbBatches = dAOBatchesOrders.findBatches(orderId, dbOrder.getNumberOfBatches());
		
		DtoOrders dtoOrder = new DtoOrders();
		dtoOrder.setOrder(dbOrder);
		dtoOrder.setBatches(dbBatches);
		
		return dtoOrder;
	}
	
	
	public DtoOrders findOrderByOrderId(String orderSourceType, Long orderId) {
		
		EntityOrder dbOrder = dAOOrders.findOrderByOrderId(orderSourceType, orderId);
		
		if(dbOrder != null) {				
			//List<DtoBatchAndProductVariant> dbBatches = dAOBatchesOrders.findBatches(orderId, dbOrder.getNumberOfBatches());
			List<EntityBatchesOrders> dbBatches = dAOBatchesOrders.findBatches(orderId, dbOrder.getNumberOfBatches());
		
			DtoOrders dtoOrder = new DtoOrders();
			dtoOrder.setOrder(dbOrder);
			dtoOrder.setBatches(dbBatches);
		
			return dtoOrder;
		}		
		return null;		
	}
	
	
	
	
	@Transactional
	public List<DtoOrders> getOrderSalesReturn(Long orderId) {
		
		List<EntityOrder> dbOrders = dAOOrders.getOrderSalesReturn(orderId);
		
		List<DtoOrders> dtoOrders = new ArrayList<DtoOrders>();
		
		for(EntityOrder dbOrder : dbOrders) {
			
			//List<DtoBatchAndProductVariant> dbBatches = dAOBatchesOrders.findBatches(dbOrder.getOrderId(), dbOrder.getNumberOfBatches());			
			List<EntityBatchesOrders> dbBatches = dAOBatchesOrders.findBatches(dbOrder.getOrderId(), dbOrder.getNumberOfBatches());
			
			DtoOrders dtoOrder = new DtoOrders();
			dtoOrder.setOrder(dbOrder);
			dtoOrder.setBatches(dbBatches);
			
			dtoOrders.add(dtoOrder);
		}
		
		return dtoOrders;
	}

		
	@Transactional
	public void updateOrder(DtoOrders dto) {
	
		//List<DtoBatchAndProductVariant> batches = dto.getBatches();
		List<EntityBatchesOrders> batches = dto.getBatches();
		EntityOrder order = dto.getOrder();
		
		dAOOrders.updateOrder(order);
		dAOBatchesOrders.updateBatches(batches);	
	}

	
	@Transactional
	public List<DtoOrders> findBatchesOrdersInDatesRange(java.util.Date fromDate, java.util.Date toDate, String customerType) {
		
		List<EntityOrder> dbOrders = dAOOrders.findOrdersInDatesRange(fromDate, toDate, customerType);	
		List<DtoOrders> dtoOrders = new ArrayList<DtoOrders>();
		
		for(EntityOrder order : dbOrders) {	
		//	List<DtoBatchAndProductVariant> dbBatches = dAOBatchesOrders.findBatches(order.getOrderId(), order.getNumberOfBatches());
			List<EntityBatchesOrders> dbBatches = dAOBatchesOrders.findBatches(order.getOrderId(), order.getNumberOfBatches());			
			DtoOrders tempDtoOrder = new DtoOrders();			
			tempDtoOrder.setOrder(order);
			tempDtoOrder.setBatches(dbBatches);	
			dtoOrders.add(tempDtoOrder);
		}				
		return dtoOrders;
	}
	
	
	@Transactional
	public List<EntityOrder> getOrdersWithPendingPayments(String customerType) {
		
		return dAOOrders.getOrdersWithPendingPayments(customerType);

	}
	
	
	@Transactional
	public ByteArrayInputStream findOrdersInDatesRangeDownload(java.util.Date fromDate, java.util.Date toDate, String customerType) {
		List<EntityOrder> orders = dAOOrders.findOrdersInDatesRange(fromDate, toDate, customerType);
		return csvHelper.ordersToCSV(orders);
	}
	
	
	@Transactional
	public ByteArrayInputStream findProductSalesInDatesRangeDownload(java.util.Date fromDate, java.util.Date toDate, String customerType) {
		
		List<?> batches = dAOOrders.findProductsSalesInDatesRange(fromDate, toDate, customerType);		
		return csvHelper.batchesToCSV(batches);
	}


	public ResponseEntity<Resource> getNewOrderAudioFile() {

	    Resource resource = new ClassPathResource("/new_order_buzzer.mp3");
	    return ResponseEntity.ok()
	           .contentType(MediaType.APPLICATION_OCTET_STREAM)
	           .body(resource);
	}



	
	/*
	public ResponseOrders findAllOrdersSalesOnlyOnlinePhone(Integer itemsPerPage, Integer startIndex) {
		ResponseOrders responseOrders = new ResponseOrders();
		
		List<DtoOrders> dtoOrders = new ArrayList<DtoOrders>();	
		List<EntityOrder> orders = dAOOrders.findAllOrdersSalesOnlyOnlinePhone(itemsPerPage, startIndex);
			
		for(EntityOrder order : orders) {
				
			List<DtoBatchAndProductVariant> dbBatches = dAOBatchesOrders.findBatches(order.getOrderId(), order.getNumberOfBatches());	
			DtoOrders dtoOrder = new DtoOrders();
			dtoOrder.setOrder(order);
			
			dtoOrder.setBatches(dbBatches);			
			dtoOrders.add(dtoOrder);
		}
		
		if(startIndex == 0) {
			Long countResults = dAOOrders.countOfAllOrdersSalesOnlyOnlinePhone();
			responseOrders.setCountOfOrders(countResults);
		}	
		
		responseOrders.setDtos(dtoOrders);
		
		return responseOrders;
	}
	*/

	
}
