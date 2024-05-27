package springboot.adminTenant.purchase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class DAOBatchesPurchase {

	private EntityManager entityManager;
	
	//set up constructor injection	
	public DAOBatchesPurchase() {	
	}
	
	@Autowired
	public DAOBatchesPurchase(EntityManager theEntityManager) {	
		this.entityManager = theEntityManager;
	}
	
	
	public void save(EntityBatchesPurchase batch) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		//first find if duplicate batcnNo exists in db. if yes, then modify the batchNo..
		
		batch.setBatchNo(checkAndUpdateDuplicateBatchNo(batch.getBatchNo(), batch.getBatchVariantId()));
		
		 EntityBatchesPurchase dbBatch = entityManager.merge(batch);	
		 entityManager.flush();
		 
		 //update quantity in variant
		Query<EntityProductVariantByUnitPurchase> theQueryVariant = currentSession.createQuery("from EntityProductVariantByUnitPurchase where variantId =:variantId");
		theQueryVariant.setParameter("variantId", batch.getBatchVariantId());
		
		EntityProductVariantByUnitPurchase dbVariant = theQueryVariant.getSingleResult();
		
		dbVariant.setQuantity(dbVariant.getQuantity() + batch.getQuantity());	
		entityManager.merge(dbVariant);

	}
		
	
	public void saveInitialStock(EntityBatchesPurchase batch) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		 EntityBatchesPurchase dbBatch = entityManager.merge(batch);	
		 entityManager.flush();
	}

	
	public List<EntityBatchesPurchase> findBatches(Long purchaseInvoiceId, Integer numberOfBatches) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query theQuery =  currentSession.createQuery("FROM EntityBatchesPurchase WHERE batchPurchaseInvoiceId =:batchPurchaseInvoiceId "
				+ "ORDER BY batchPurchaseInvoiceId DESC");
		theQuery.setParameter("batchPurchaseInvoiceId",purchaseInvoiceId);
		theQuery.setMaxResults(numberOfBatches);
	//	theQuery.setParameter("transactionStatus","batch-deleted");
		
		List<EntityBatchesPurchase> dbBatchesPurchase = theQuery.getResultList();
	    
	    Iterator itr = dbBatchesPurchase.iterator();
	    while (itr.hasNext()) { 	
	    	EntityBatchesPurchase dbBatchIter = (EntityBatchesPurchase)itr.next();
	         if (dbBatchIter.getTransactionStatus() != null) {
	        	 if (dbBatchIter.getTransactionStatus().equals("batch-deleted") ) {
	        		 itr.remove();
	        	 }
	         }  
	    } 
		return dbBatchesPurchase;
	}

	
	public void savePurchaseReturn(EntityBatchesPurchase batch) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		batch.setTransactionStatus("batch-returned");		
		
		EntityBatchesPurchase dbBatch = entityManager.merge(batch);
		
		//update current quantity in variant as well as purchase batch..
		
		Query theQueryPurchaseBatch = currentSession.createQuery("from EntityBatchesPurchase where batchNo =:batchNo AND batchPurSaleBool=:batchPurSaleBool"
				+ " AND batchVariantId=:batchVariantId ORDER BY batchId ");
		theQueryPurchaseBatch.setParameter("batchNo", dbBatch.getBatchNo());
		theQueryPurchaseBatch.setParameter("batchPurSaleBool", 0);
		theQueryPurchaseBatch.setParameter("batchVariantId", dbBatch.getBatchVariantId());
		theQueryPurchaseBatch.setMaxResults(1);
		
		EntityBatchesPurchase dbPurchaseBatch = (EntityBatchesPurchase) theQueryPurchaseBatch.uniqueResult();
		
		dbPurchaseBatch.setCurrentQuantity(dbPurchaseBatch.getCurrentQuantity() - dbBatch.getQuantity());
		entityManager.merge(dbPurchaseBatch);
		
		Query theQueryVariant = currentSession.createQuery("from EntityProductVariantByUnitPurchase where variantId =:variantId");
		theQueryVariant.setParameter("variantId", dbBatch.getBatchVariantId());
		
		EntityProductVariantByUnitPurchase dbVariant = (EntityProductVariantByUnitPurchase) theQueryVariant.getSingleResult();
		
		dbVariant.setQuantity(dbVariant.getQuantity() - dbBatch.getQuantity());	
		entityManager.merge(dbVariant);
		
	}
	
	
	public void updateBatches(List<EntityBatchesPurchase> batches) {
		Session currentSession = entityManager.unwrap(Session.class);

		Long purchaseInvoiceId = batches.get(0).getBatchPurchaseInvoiceId();
		
		Query theQuery =
				  currentSession.createQuery("from EntityBatchesPurchase where batchPurchaseInvoiceId =:batchPurchaseInvoiceId");
		theQuery.setParameter("batchPurchaseInvoiceId",purchaseInvoiceId);

		List<EntityBatchesPurchase> dbBatches = theQuery.getResultList();
		
		for(EntityBatchesPurchase dbBatch : dbBatches) {
			
			int batchFound = 0;
			
			for(EntityBatchesPurchase batch : batches) {
				if(batch.getBatchId().equals( dbBatch.getBatchId() ) ) {
					batchFound = 1;
					
					//current quantity has to be corrected in this batch and variant now..
			
					dbBatch.setCurrentQuantity(dbBatch.getCurrentQuantity() - dbBatch.getQuantity() + batch.getQuantity());
					entityManager.merge(dbBatch);
					
					Query theQueryVariant = currentSession.createQuery("from EntityProductVariantByUnitPurchase where variantId =:variantId");
					theQueryVariant.setParameter("variantId", dbBatch.getBatchVariantId());
					
					EntityProductVariantByUnitPurchase dbVariant = (EntityProductVariantByUnitPurchase) theQueryVariant.getSingleResult();
					
					dbVariant.setQuantity(dbVariant.getQuantity() - dbBatch.getQuantity() + batch.getQuantity());	
					entityManager.merge(dbVariant);
					
					//now update the purchase batch
					
					dbBatch.setQuantity(batch.getQuantity());
					dbBatch.setBatchPurchasePrice(batch.getBatchPurchasePrice());
					dbBatch.setMrp(batch.getMrp());
					dbBatch.setSellingPrice(batch.getSellingPrice());
					dbBatch.setPpIncludesGST(batch.getPpIncludesGST());
					dbBatch.setExpiryDate(batch.getExpiryDate());
					
					currentSession.saveOrUpdate(dbBatch);						
				}
			}
			if(batchFound == 0) {
				dbBatch.setTransactionStatus("batch-deleted");	
				currentSession.saveOrUpdate(dbBatch);
				
				//update quantity in purchase batch and variant, although according to the front-end code, we are not allowing any batch to get deleted.
				
				dbBatch.setCurrentQuantity(dbBatch.getCurrentQuantity() - dbBatch.getQuantity());
				entityManager.merge(dbBatch);
				
				Query<EntityProductVariantByUnitPurchase> theQueryVariant = currentSession.createQuery("from EntityProductVariantByUnitPurchase where variantId =:variantId");
				theQueryVariant.setParameter("variantId", dbBatch.getBatchVariantId());
				
				EntityProductVariantByUnitPurchase dbVariant = theQueryVariant.getSingleResult();
				
				dbVariant.setQuantity(dbVariant.getQuantity() - dbBatch.getQuantity());	
				entityManager.merge(dbVariant);
			}
		}	
			
		for(EntityBatchesPurchase batch : batches) {				
			if (batch.getBatchId() == 0) {	
				//add new batch entry
				
				batch.setDisplay(true);
				
			//	batch2.setBatchNo(batch.getBatchNo());
				batch.setBatchNo(checkAndUpdateDuplicateBatchNo(batch.getBatchNo(), batch.getBatchVariantId()));

				currentSession.save(batch);
				
				//update the quantity in variant..
				
				Query<EntityProductVariantByUnitPurchase> theQueryVariant = currentSession.createQuery("from EntityProductVariantByUnitPurchase where variantId =:variantId");
				theQueryVariant.setParameter("variantId", batch.getBatchVariantId());
				
				EntityProductVariantByUnitPurchase dbVariant = theQueryVariant.getSingleResult();
				
				dbVariant.setQuantity(dbVariant.getQuantity() + batch.getQuantity());	
				entityManager.merge(dbVariant);				
				
			}			
		}				
	}
	
	
	public void updateInitialStockBatches(List<EntityBatchesPurchase> batches) {
		Session currentSession = entityManager.unwrap(Session.class);
			
		for(EntityBatchesPurchase batch : batches) {				
	
			//add new batch entry			
			EntityBatchesPurchase batch2 = new EntityBatchesPurchase();			
			batch.setDisplay(true);
			currentSession.save(batch);
		}			
	}
		
	
	public String checkAndUpdateDuplicateBatchNo (String checkBatchNo, Integer variantId) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		String updatedBatchNo = "";
		
		Query theQueryBatches = currentSession.createQuery("FROM EntityBatchesPurchase WHERE batchNo =:batchNo "
				+ " AND batchVariantId =:variantId");
		theQueryBatches.setParameter("batchNo", checkBatchNo);
		theQueryBatches.setParameter("variantId", variantId);

		List<EntityBatchesPurchase> dbBatches = theQueryBatches.getResultList();

		if(dbBatches.size() == 0) {			
		//	batch2.setBatchNo(batch.getBatchNo());
			return checkBatchNo;
		}
		else {	
			int iter = 1;
			String batchNo = checkBatchNo;

			while(true){		
				int insertionIndex = 10;				
				String stringToInsert = "-" +iter;

				String newBatchNo = batchNo.substring(0, insertionIndex) + stringToInsert;


				Query theQueryNewBatch = currentSession.createQuery("from EntityBatchesPurchase where batchNo =:batchNo "
						+ " AND batchVariantId =:variantId");				
				theQueryNewBatch.setParameter("batchNo", newBatchNo);
				theQueryNewBatch.setParameter("variantId", variantId);

				List<EntityBatchesPurchase> dbNewBatches = theQueryNewBatch.getResultList();

				if(dbNewBatches.size() == 0) {					
				//	batch2.setBatchNo(newBatchNo);
					updatedBatchNo = newBatchNo;
					break;
				}
				else {
					iter = iter + 1;
				}
			}			
		}
				
		return updatedBatchNo;		
	}
	
}	  
		


