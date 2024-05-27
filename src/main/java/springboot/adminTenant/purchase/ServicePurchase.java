package springboot.adminTenant.purchase;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ServicePurchase {

	private DAOPurchase dAOPurchase;
	private DAOBatchesPurchase dAOBatchesPurchase;
	
	@Autowired
	public ServicePurchase(DAOPurchase theDAOPurchase, DAOBatchesPurchase theDAOBatchesPurchase) {		
		dAOPurchase = theDAOPurchase;
		dAOBatchesPurchase = theDAOBatchesPurchase;
	}

	
	@Transactional
	public void save(DtoPurchase dtoPurchase) {
		
	    long millis = System.currentTimeMillis();  
		java.sql.Date todayDate = new java.sql.Date(millis);
			
		EntityPurchaseInvoice purchaseInvoice = dtoPurchase.getPurchaseInvoice();
		purchaseInvoice.setPurchaseInvoiceId((long) 0);
		purchaseInvoice.setDateCreated(todayDate);
		
		EntityPurchaseInvoice dbPurchaseInvoice = dAOPurchase.save(purchaseInvoice);
		
		List<EntityBatchesPurchase> batches = dtoPurchase.getBatches();
		
		for(EntityBatchesPurchase batch : batches) {
			batch.setBatchId((long) 0);
			batch.setBatchPurchaseInvoiceId(dbPurchaseInvoice.getPurchaseInvoiceId()); 	
			dAOBatchesPurchase.save(batch);			
		}			
	}
	
	
	
	@Transactional
	public void saveInitialStockPurchase(DtoPurchase dto) {
		
	    long millis = System.currentTimeMillis();  
		java.sql.Date todayDate = new java.sql.Date(millis);
			
		EntityPurchaseInvoice purchaseInvoice = dto.getPurchaseInvoice();
		purchaseInvoice.setPurchaseInvoiceId((long) 0);
		purchaseInvoice.setDateCreated(todayDate);
		
		EntityPurchaseInvoice dbPurchaseInvoice = dAOPurchase.save(purchaseInvoice);
		
		List<EntityBatchesPurchase> batches = dto.getBatches();
		
		for(EntityBatchesPurchase batch : batches) {
			batch.setBatchId((long) 0);
			batch.setBatchPurchaseInvoiceId(dbPurchaseInvoice.getPurchaseInvoiceId()); 	
			dAOBatchesPurchase.saveInitialStock(batch);			
		}		
	}
	

	@Transactional
	public DtoPurchase findById(Long purchaseInvoiceId) {
		
		EntityPurchaseInvoice dbPurchase = dAOPurchase.findById(purchaseInvoiceId);
		
		List<EntityBatchesPurchase> dbBatches = dAOBatchesPurchase.findBatches(purchaseInvoiceId, dbPurchase.getNumberOfBatches());
		
		DtoPurchase dtoPurchase = new DtoPurchase();
		dtoPurchase.setPurchaseInvoice(dbPurchase);
		dtoPurchase.setBatches(dbBatches);
		
		return dtoPurchase;
	}

	
	@Transactional
	public List<DtoPurchase> getPurchaseInvoiceAndReturn(Long purchaseInvoiceId) {

		List<EntityPurchaseInvoice> dbPurchases = dAOPurchase.getPurchaseInvoiceAndReturn(purchaseInvoiceId);
		
		List<DtoPurchase> dtoPurchases = new ArrayList<DtoPurchase>();
		
		for(EntityPurchaseInvoice dbPurchase : dbPurchases) {
			
			List<EntityBatchesPurchase> dbBatches = dAOBatchesPurchase.findBatches(purchaseInvoiceId, dbPurchase.getNumberOfBatches());
			
			DtoPurchase dtoPurchase = new DtoPurchase();
			dtoPurchase.setPurchaseInvoice(dbPurchase);
			dtoPurchase.setBatches(dbBatches);
			
			dtoPurchases.add(dtoPurchase);
		}
		
		return dtoPurchases;
	}

	
	@Transactional
	public List<DtoPurchase> savePurchaseReturn(DtoPurchase dto) {
		
	    long millis = System.currentTimeMillis();  
		Date todayDate = new Date(millis);
		
		EntityPurchaseInvoice purchase = dto.getPurchaseInvoice();
		purchase.setPurchaseInvoiceId((long) 0);	
		
		purchase.setDateCreated(todayDate);

		EntityPurchaseInvoice dbPurchaseReturns = dAOPurchase.savePurchaseReturn(purchase);
		List<EntityBatchesPurchase> batches = dto.getBatches();
		
		for(EntityBatchesPurchase batch : batches) {	
			batch.setBatchPurchaseInvoiceId(dbPurchaseReturns.getPurchaseInvoiceId()); 
			dAOBatchesPurchase.savePurchaseReturn(batch);
		}
		
		return null;
	}

	
	@Transactional
	public ResponsePurchases findAllPurchases(Integer itemsPerPage, Integer startIndex) {
		ResponsePurchases responsePurchases = new ResponsePurchases();
		
		List<DtoPurchase> dtoPurchases = new ArrayList<DtoPurchase>();	
		List<EntityPurchaseInvoice> purchases = dAOPurchase.findAllPurchases(itemsPerPage, startIndex);
			
		for(EntityPurchaseInvoice purchase : purchases) {
		
			Long purchaseInvoiceId = purchase.getPurchaseInvoiceId();		
			List<EntityBatchesPurchase> dbBatches = dAOBatchesPurchase.findBatches(purchaseInvoiceId, purchase.getNumberOfBatches());	
			DtoPurchase dtoPurchase = new DtoPurchase();
			dtoPurchase.setPurchaseInvoice(purchase);
			
			dtoPurchase.setBatches(dbBatches);			
			dtoPurchases.add(dtoPurchase);
		}
		
		if(startIndex == 0) {
			Long countResults = dAOPurchase.countOfAllPurchases();
			responsePurchases.setCountOfPurchases(countResults);
		}	
		
		responsePurchases.setDtos(dtoPurchases);
		
		return responsePurchases;
	}

	
	@Transactional
	public void updatePurchase(DtoPurchase dto) {
		
		List<EntityBatchesPurchase> batches = dto.getBatches();
		EntityPurchaseInvoice purchase = dto.getPurchaseInvoice();		
		dAOPurchase.updatePurchase(purchase);
		dAOBatchesPurchase.updateBatches(batches);
		
	}
	
	
	@Transactional
	public void updateInitialStockPurchase(DtoPurchase dto) {
		
		List<EntityBatchesPurchase> batches = dto.getBatches();		
		dAOBatchesPurchase.updateInitialStockBatches(batches);		
	}
	

	@Transactional
	public DtoPurchase findInitialStockPurchaseInvoice(java.util.Date fromDate, java.util.Date toDate) {
				
		EntityPurchaseInvoice dbPurchase = dAOPurchase.findInitialStockPurchaseInvoice(fromDate, toDate);
		
		if(dbPurchase != null){	
			List<EntityBatchesPurchase> dbBatches = dAOBatchesPurchase.findBatches(dbPurchase.getPurchaseInvoiceId(), dbPurchase.getNumberOfBatches());
			
			DtoPurchase dtoPurchase = new DtoPurchase();
			dtoPurchase.setPurchaseInvoice(dbPurchase);
			dtoPurchase.setBatches(dbBatches);
			
			return dtoPurchase;
		}
		else {
		return null;
		}	
	}
}
