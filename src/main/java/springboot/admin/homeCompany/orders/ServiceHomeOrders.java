package springboot.admin.homeCompany.orders;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class ServiceHomeOrders {
	
	private DAOHomeOrders dAOHomeOrders;

    @Autowired
    public ServiceHomeOrders(DAOHomeOrders dAOHomeOrders) {
        this.dAOHomeOrders = dAOHomeOrders;
    }

    @Transactional
	public ResponseOrdersHome findAllOrders(Integer itemsPerPage, Integer startIndex) throws SQLException {
		   	
    	return dAOHomeOrders.findAllOrders(itemsPerPage, startIndex);
	}
    
    @Transactional
	public List<DtoOrderAndBatchesHome> findOrdersAndBatchesInDatesRange(Date fromDate, Date toDate, String customerType) throws SQLException {
    	
    	return dAOHomeOrders.findOrdersAndBatchesInDatesRange(fromDate, toDate, customerType);

    	/*
		List<EntityOrderHome> dbOrders = dAOHomeOrders.findOrdersInDatesRange(fromDate, toDate, customerType);	
		List<DtoOrderAndBatchesHome> dtoOrders = new ArrayList<>();
		
		for(EntityOrderHome order : dbOrders) {	
			List<DtoBatchAndProduct> dbBatches = dAOHomeBatches.findBatches(order.getOrderId());	
			DtoOrders tempDtoOrder = new DtoOrders();			
			tempDtoOrder.setOrder(order);
			tempDtoOrder.setBatches(dbBatches);	
			dtoOrders.add(tempDtoOrder);
		}				
		return dtoOrders;
		*/
	}
	

    @Transactional
	public Boolean cancelOrder(Long orderId) throws SQLException {

		return dAOHomeOrders.cancelOrder(orderId);
	}

	public ResponseOrdersHome findPendingOrders(Integer itemsPerPage, Integer startIndex) throws SQLException {
		
		return dAOHomeOrders.findPendingOrders(itemsPerPage, startIndex);
	}
	

    @Transactional
	public ResponseOrdersHome getOrdersOfCustomer(Integer customerId, Integer itemsPerPage, Integer startIndex) throws SQLException {
		
		return dAOHomeOrders.findOrdersOfCustomer(customerId, itemsPerPage, startIndex);
	}

    @Transactional
	public DtoOrderAndBatchesHome findOrderByOrderId(Long orderId) throws SQLException {

    	return dAOHomeOrders.findOrderByOrderId(orderId);
	}
    
    @Transactional
	public ResponseOrdersHome getOrdersOfTenant(Integer tenantId, Integer itemsPerPage, Integer startIndex) throws SQLException {
		
		return dAOHomeOrders.findOrdersOfTenant(tenantId, itemsPerPage, startIndex);
	}
	

    
    /*
    @Transactional
	public List<DtoOrders> findAllPendingOrders(String orderSourceType) {
		List<DtoOrders> dtoOrders = new ArrayList<DtoOrders>();	
		List<EntityOrder> orders = dAOHomeOrders.findAllPendingOrders(orderSourceType);
			
		for(EntityOrder order : orders) {
		
			int orderId = order.getOrderId();		
			List<DtoBatchAndProduct> dbBatches = dAOHomeBatches.findBatches(orderId);	
			DtoOrders dtoOrder = new DtoOrders();
			dtoOrder.setOrder(order);
			
			dtoOrder.setBatches(dbBatches);			
			dtoOrders.add(dtoOrder);
		}			
		return dtoOrders;
	}

    @Transactional
	public InputStream findOrdersInDatesRangeDownload(Date fromDate, Date toDate, String customerType) {
		List<EntityOrder> orders = dAOHomeOrders.findOrdersInDatesRange(fromDate, toDate, customerType);
		return csvHelper.ordersToCSV(orders);
	}
    
    @Transactional
	public InputStream findProductSalesInDatesRangeDownload(Date fromDate, Date toDate, String customerType) {
		List<?> batches = dAOHomeOrders.findProductsSalesInDatesRange(fromDate, toDate, customerType);		
		return csvHelper.batchesToCSV(batches);
	}


	
*/	

}
