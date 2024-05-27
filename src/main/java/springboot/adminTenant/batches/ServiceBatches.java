package springboot.adminTenant.batches;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import springboot.adminTenant.orders.DtoOrders;
import springboot.adminTenant.orders.EntityOrder;
import springboot.adminTenant.orders.csvHelper;


@Service
public class ServiceBatches {

	private DAOBatches dAOBatches;
	
	@Autowired
	public ServiceBatches(DAOBatches theDAOBatches) {		
		dAOBatches = theDAOBatches;
	}

	
	@Transactional
	public ResponseBatches findBatches(Integer variantId, Integer itemsPerPage, Integer startIndex) {
		
		ResponseBatches responseBatches = new ResponseBatches();

		List<DTOVariantBatches> dtos = dAOBatches.findBatches(variantId, itemsPerPage, startIndex);
		responseBatches.setDtos(dtos);
				
		if(startIndex == 0) {
			Long countResults = dAOBatches.countOfAllBatches(variantId);
			responseBatches.setCountOfBatches(countResults);
		}
		
		return responseBatches;
	}

	
	@Transactional
	public ResponseBatches getPurchaseBatches(Integer variantId, Integer startIndex, Integer itemsPerPage) {
		
		ResponseBatches responseBatches = new ResponseBatches();

		List<DTOVariantBatches> dtos = dAOBatches.getPurchaseBatches(variantId, startIndex, itemsPerPage);
		responseBatches.setDtos(dtos);
				
		if(startIndex == 0) {
			Long countResults = dAOBatches.countOfPurchaseBatches(variantId);
			responseBatches.setCountOfBatches(countResults);
		}		

		return responseBatches;
	}
	
	
	@Transactional
	public void updateBatches(DTOBatchesUpdate dtoBatches) {
		
		updateStock(dtoBatches.getStockUpdateEntries());
		
		for(DTOSellingPrice sellingPriceEntry : dtoBatches.getSellingPriceEntries()) {			
			updateSellingPriceOfBatch(sellingPriceEntry.getBatchNo(), sellingPriceEntry.getBatchVariantId(), sellingPriceEntry.getSellingPrice());
		}
		
		for(DTOMrp mrpEntry : dtoBatches.getMrpEntries()) {			
			updateMrpOfBatch(mrpEntry.getBatchNo(), mrpEntry.getBatchVariantId(), mrpEntry.getMrp());
		}
		
		
	}
	
	@Transactional
	private void updateMrpOfBatch(String batchNo, Integer variantId, Float mrp) {
		
		dAOBatches.updateMrpOfBatch(batchNo, variantId, mrp);
		
	}

	
	@Transactional
	public void updateSellingPriceOfBatch(String batchNo, Integer variantId, Float sellingPrice) {
		
		dAOBatches.updateSellingPriceOfBatch(batchNo, variantId, sellingPrice);
	}
	

	
	@Transactional
	public void batchDisplayChange(String batchNo, Integer variantId) {
		
		dAOBatches.batchDisplayChange(batchNo, variantId);
	}

	
	@Transactional
	public void updateStock(List<DTOStockUpdate> stockUpdateData) {
		
		for(DTOStockUpdate batchUpdate : stockUpdateData) {
			
		    long millis = System.currentTimeMillis();  
			java.sql.Date todayDate = new java.sql.Date(millis); 			
			batchUpdate.setBatchEntryDate(todayDate);
			
			dAOBatches.updateStock(batchUpdate);
		}		
	}
	

	
	@Transactional
	public List<EntityBatch> findExpiryStockInDatesRange(Date fromDate, Date toDate) {
		// TODO Auto-generated method stub
		return dAOBatches.findExpiryStockInDatesRange(fromDate, toDate);
	}

	
	@Transactional
	public InputStream downloadNonPerishablesOrder(Integer brandId) {
	//	List<EntityOrder> orders = dAOOrders.findOrdersInDatesRange(fromDate, toDate);
		
		List<DTONonPerishablesOrder> orderStock = dAOBatches.downloadNonPerishablesOrder(brandId);
		
		return csvHelperBatches.orderStockToCSV(orderStock);
	}







}
