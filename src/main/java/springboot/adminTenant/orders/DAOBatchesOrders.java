 package springboot.adminTenant.orders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DAOBatchesOrders {

	private EntityManager entityManager;
	
	//set up constructor injection	
	public DAOBatchesOrders() {	
	}
	
	@Autowired
	public DAOBatchesOrders(EntityManager theEntityManager) {	
		this.entityManager = theEntityManager;
	}
	
	
	
	public void save(EntityBatchesOrders batch) {
		
		Session currentSession = entityManager.unwrap(Session.class);
			
		 EntityBatchesOrders dbBatch = entityManager.merge(batch);	
		 
		 //need to update variant quantity and purchase batches quantity now..
		 			
		Query theQuery = currentSession.createQuery("from EntityProductVariantByUnitOrders where variantId =:variantId");
			theQuery.setParameter("variantId",batch.getBatchVariantId());
			
		EntityProductVariantByUnitOrders dbVariant = (EntityProductVariantByUnitOrders) theQuery.getSingleResult();	
		
		dbVariant.setQuantity(dbVariant.getQuantity() - batch.getQuantity());
		entityManager.merge(dbVariant);
		
		
		Query theQueryBatch = currentSession.createQuery("from EntityBatchesOrders where batchVariantId =:variantId AND batchNo=:batchNo AND batchPurSaleBool=:batchPurSaleBool");
		theQueryBatch.setParameter("variantId", batch.getBatchVariantId());
		theQueryBatch.setParameter("batchNo", batch.getBatchNo());
		theQueryBatch.setParameter("batchPurSaleBool", 0);
		
		EntityBatchesOrders dbPurchaseBatch = (EntityBatchesOrders) theQueryBatch.getSingleResult();	
	
		dbPurchaseBatch.setCurrentQuantity(dbPurchaseBatch.getCurrentQuantity() - batch.getQuantity());
		entityManager.merge(dbPurchaseBatch);		 
	}
	

	
	public void saveSalesReturn(EntityBatchesOrders batch) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		batch.setTransactionStatus("batch-returned");		
		
		EntityBatchesOrders dbBatch = entityManager.merge(batch);
		
		//update current quantity in corresponding purchase batch and also in variant
		
		Query theQueryPurchaseBatch = currentSession.createQuery("from EntityBatchesOrders where batchNo =:batchNo AND batchPurSaleBool=:batchPurSaleBool"
				+ " AND batchVariantId=:batchVariantId ");
		theQueryPurchaseBatch.setParameter("batchNo", dbBatch.getBatchNo());
		theQueryPurchaseBatch.setParameter("batchPurSaleBool", 0);
		theQueryPurchaseBatch.setParameter("batchVariantId", dbBatch.getBatchVariantId());
		
		EntityBatchesOrders dbPurchaseBatch = (EntityBatchesOrders) theQueryPurchaseBatch.getSingleResult();
		
		dbPurchaseBatch.setCurrentQuantity(dbPurchaseBatch.getCurrentQuantity() + dbBatch.getQuantity());
		entityManager.merge(dbPurchaseBatch);
		
		Query<EntityProductVariantByUnitOrders> theQueryVariant = currentSession.createQuery("from EntityProductVariantByUnitOrders where variantId =:variantId");
		theQueryVariant.setParameter("variantId", dbBatch.getBatchVariantId());
		
		EntityProductVariantByUnitOrders dbVariant = theQueryVariant.getSingleResult();
		
		dbVariant.setQuantity(dbVariant.getQuantity() + dbBatch.getQuantity());	
		entityManager.merge(dbVariant);
				
	}
	
	
	
	public List<EntityBatchesOrders> findBatches(Long orderId, Integer numberOfBatches) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query theQuery = currentSession.createQuery("FROM EntityBatchesOrders WHERE batchOrderId =:batchOrderId "
				+ " AND ( transactionStatus IS NULL OR (transactionStatus IS NOT NULL AND transactionStatus <>:transactionStatus)) ORDER BY batchId DESC");
				
		theQuery.setParameter("batchOrderId",orderId);
		theQuery.setParameter("transactionStatus", "batch-deleted");		
		theQuery.setMaxResults(numberOfBatches);
	//	theQuery.setParameter("transactionStatus","batch-deleted");
		
		
		List<EntityBatchesOrders> dbBatches = theQuery.getResultList();
		
		Collections.reverse(dbBatches);
		/*
	    Iterator itr = dbBatches.iterator();
	    while (itr.hasNext()) { 	
	    	EntityBatchesOrders dbBatchIter = (EntityBatchesOrders)itr.next();
	         if (dbBatchIter.getTransactionStatus() != null) {
	        	 if (dbBatchIter.getTransactionStatus().equals("batch-deleted") ) {
	        		 itr.remove();
	        	 }
	         }  
	    } 
	    */
		return dbBatches;
	}	
	

	 
	public void cancelOrder(Long orderId, Integer numberOfBatches) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query<EntityBatchesOrders> theQuery =
				  currentSession.createQuery("FROM EntityBatchesOrders WHERE batchOrderId =:batchOrderId ORDER BY batchId DESC");
		theQuery.setMaxResults(numberOfBatches);
		theQuery.setParameter("batchOrderId",orderId);
				  
		//execute the query and get result list 
		List<EntityBatchesOrders> dbBatches = theQuery.getResultList();
		
		for(EntityBatchesOrders batch : dbBatches) {	
			if(batch.getTransactionStatus() == null || ( batch.getTransactionStatus() != null && !batch.getTransactionStatus().equals("batch-deleted")) ) {
				batch.setTransactionStatus("order-cancelled");
				currentSession.saveOrUpdate(batch);	
				currentSession.flush();
				
				Query theQueryPurchaseBatch = currentSession.createQuery("from EntityBatchesOrders where batchNo =:batchNo AND batchPurSaleBool=:batchPurSaleBool"
						+ " AND batchVariantId=:batchVariantId ");
				theQueryPurchaseBatch.setParameter("batchNo", batch.getBatchNo());
				theQueryPurchaseBatch.setParameter("batchPurSaleBool", 0);	
				theQueryPurchaseBatch.setParameter("batchVariantId", batch.getBatchVariantId());
				
				EntityBatchesOrders dbPurchaseBatch = (EntityBatchesOrders) theQueryPurchaseBatch.getSingleResult();
				
				dbPurchaseBatch.setCurrentQuantity(dbPurchaseBatch.getCurrentQuantity() + batch.getQuantity());
				entityManager.merge(dbPurchaseBatch);
				
				Query<EntityProductVariantByUnitOrders> theQueryVariant = currentSession.createQuery("from EntityProductVariantByUnitOrders where variantId =:variantId");
				theQueryVariant.setParameter("variantId", batch.getBatchVariantId());
				
				EntityProductVariantByUnitOrders dbVariant = theQueryVariant.getSingleResult();
				
				dbVariant.setQuantity(dbVariant.getQuantity() + batch.getQuantity());
				entityManager.merge(dbVariant);		
			}	
			
		}	
	}
	
	 
	public void deliverOrder(Long orderId, Integer numberOfBatches) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query theQuery =
				  currentSession.createQuery("from EntityBatchesOrders where batchOrderId =:batchOrderId ORDER BY batchId DESC");
		theQuery.setParameter("batchOrderId",orderId);
		theQuery.setMaxResults(numberOfBatches);
		
		//execute the query and get result list 
		List<EntityBatchesOrders> dbBatches = theQuery.getResultList();
		
		for(EntityBatchesOrders batch : dbBatches) {
			if(batch.getTransactionStatus() == null || ( batch.getTransactionStatus() != null && !batch.getTransactionStatus().equals("batch-deleted")) ) {
				batch.setTransactionStatus("order-delivered");
				currentSession.saveOrUpdate(batch);	
				currentSession.flush();
			}			
		}	
	}
	
	 
	public void completeOrder(Long orderId, Integer numberOfBatches) {
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query<EntityBatchesOrders> theQuery =
				  currentSession.createQuery("from EntityBatchesOrders where batchOrderId =:batchOrderId  ORDER BY batchId DESC");
		theQuery.setParameter("batchOrderId",orderId);
		theQuery.setMaxResults(numberOfBatches);
		
		//execute the query and get result list 
		List<EntityBatchesOrders> dbBatches = theQuery.getResultList();
		
		for(EntityBatchesOrders batch : dbBatches) {
			if(batch.getTransactionStatus() == null || ( batch.getTransactionStatus() != null && !batch.getTransactionStatus().equals("batch-deleted")) ) {
				batch.setTransactionStatus("order-completed");
				currentSession.saveOrUpdate(batch);
				currentSession.flush();
			}					
		}		
	}
	
	 
	public void returnOrder(Long orderId, Integer numberOfBatches) {

		Session currentSession = entityManager.unwrap(Session.class);
		
		Query<EntityBatchesOrders> theQuery =
				  currentSession.createQuery("from EntityBatchesOrders where batchOrderId =:batchOrderId ORDER BY batchId DESC");
		theQuery.setParameter("batchOrderId",orderId);
				  
		//execute the query and get result list 
		List<EntityBatchesOrders> dbBatches = theQuery.getResultList();
		
		for(EntityBatchesOrders batch : dbBatches) {
			if( batch.getTransactionStatus() == null || ( batch.getTransactionStatus() != null && !batch.getTransactionStatus().equals("batch-deleted")) ) {
				batch.setTransactionStatus("order-returned");
				currentSession.saveOrUpdate(batch);
				currentSession.flush();
				
				Query<EntityBatchesOrders> theQueryPurchaseBatch = currentSession.createQuery("from EntityBatchesOrders where batchNo =:batchNo AND batchPurSaleBool=:batchPurSaleBool"
						+ " AND batchVariantId=:batchVariantId ");
				theQueryPurchaseBatch.setParameter("batchNo", batch.getBatchNo());
				theQueryPurchaseBatch.setParameter("batchPurSaleBool", 0);	
				theQueryPurchaseBatch.setParameter("batchVariantId", batch.getBatchVariantId());
				
				EntityBatchesOrders dbPurchaseBatch = theQueryPurchaseBatch.getSingleResult();
				
				dbPurchaseBatch.setCurrentQuantity(dbPurchaseBatch.getCurrentQuantity() + batch.getQuantity());
				entityManager.merge(dbPurchaseBatch);
				
				Query<EntityProductVariantByUnitOrders> theQueryVariant = currentSession.createQuery("from EntityProductVariantByUnitOrders where variantId =:variantId");
				theQueryVariant.setParameter("variantId", batch.getBatchVariantId());
				
				EntityProductVariantByUnitOrders dbVariant = theQueryVariant.getSingleResult();
				
				dbVariant.setQuantity(dbVariant.getQuantity() + batch.getQuantity());
				entityManager.merge(dbVariant);		

				
			}				
		}		
	}

	
	public void updateBatches(List<EntityBatchesOrders> batches) {
		
		System.out.println(batches);
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Long orderId = batches.get(0).getBatchOrderId();
		
		Query<EntityBatchesOrders> theQuery =
				  currentSession.createQuery("from EntityBatchesOrders where batchOrderId =:batchOrderId");
		theQuery.setParameter("batchOrderId",orderId);

		List<EntityBatchesOrders> dbBatches = theQuery.getResultList();
		
		for(EntityBatchesOrders dbBatch : dbBatches) {
			
			int batchFound = 0;
			
			for(EntityBatchesOrders batch : batches) {
				if(batch.getBatchId().equals(dbBatch.getBatchId())) {
					batchFound = 1;
					
					/// need to update current quantity in both purchase batch and product variant
					
					Query theQueryPurchaseBatch = currentSession.createQuery("from EntityBatchesOrders where batchNo =:batchNo AND batchPurSaleBool=:batchPurSaleBool"
							+ " AND batchVariantId=:batchVariantId");
					theQueryPurchaseBatch.setParameter("batchNo", dbBatch.getBatchNo());
					theQueryPurchaseBatch.setParameter("batchPurSaleBool", 0);	
					theQueryPurchaseBatch.setParameter("batchVariantId", dbBatch.getBatchVariantId());
					
					EntityBatchesOrders dbPurchaseBatch = (EntityBatchesOrders) theQueryPurchaseBatch.getSingleResult();
										
					dbPurchaseBatch.setCurrentQuantity(dbPurchaseBatch.getCurrentQuantity() + dbBatch.getQuantity() - batch.getQuantity());
					entityManager.merge(dbPurchaseBatch);
					
					Query theQueryVariant = currentSession.createQuery("from EntityProductVariantByUnitOrders where variantId =:variantId");
					theQueryVariant.setParameter("variantId", dbBatch.getBatchVariantId());
					
					EntityProductVariantByUnitOrders dbVariant = (EntityProductVariantByUnitOrders) theQueryVariant.getSingleResult();
					
					dbVariant.setQuantity(dbVariant.getQuantity() + dbBatch.getQuantity() - batch.getQuantity());	
					entityManager.merge(dbVariant);
					
					
					///update batch as per edited order..
					
					dbBatch.setQuantity(batch.getQuantity());
					dbBatch.setSellingPrice(batch.getSellingPrice());
					currentSession.saveOrUpdate(dbBatch);	
					currentSession.flush();		
					
				}
			}
			if(batchFound == 0) {
				dbBatch.setTransactionStatus("batch-deleted");	
				currentSession.saveOrUpdate(dbBatch);
				currentSession.flush();
				
				/// need to update current quantity in both purchase batch and product variant
				
				Query<EntityBatchesOrders> theQueryPurchaseBatch = currentSession.createQuery("from EntityBatchesOrders where batchNo =:batchNo AND batchPurSaleBool=:batchPurSaleBool"
						+ " AND batchVariantId=:batchVariantId ");
				theQueryPurchaseBatch.setParameter("batchNo", dbBatch.getBatchNo());
				theQueryPurchaseBatch.setParameter("batchPurSaleBool", 0);
				theQueryPurchaseBatch.setParameter("batchVariantId", dbBatch.getBatchVariantId());
				
				EntityBatchesOrders dbPurchaseBatch = theQueryPurchaseBatch.getSingleResult();
				
				dbPurchaseBatch.setCurrentQuantity(dbPurchaseBatch.getCurrentQuantity() + dbBatch.getQuantity());
				entityManager.merge(dbPurchaseBatch);
				
				Query<EntityProductVariantByUnitOrders> theQueryVariant = currentSession.createQuery("from EntityProductVariantByUnitOrders where variantId =:variantId");
				theQueryVariant.setParameter("variantId", dbBatch.getBatchVariantId());
				
				EntityProductVariantByUnitOrders dbVariant = theQueryVariant.getSingleResult();
				
				dbVariant.setQuantity(dbVariant.getQuantity() + dbBatch.getQuantity());	
				entityManager.merge(dbVariant);
				
			}
		}	
			
		for(EntityBatchesOrders batch : batches) {				
			if (batch.getBatchId() == 0) {	
				//add new batch entry

				currentSession.save(batch);
				currentSession.flush();
				
				// need to update current quantity in both purchase batch and product variant
				
				Query theQueryPurchaseBatch = currentSession.createQuery("from EntityBatchesOrders where batchNo =:batchNo AND batchPurSaleBool=:batchPurSaleBool"
						+ " AND batchVariantId=:batchVariantId ");
				theQueryPurchaseBatch.setParameter("batchNo", batch.getBatchNo());
				theQueryPurchaseBatch.setParameter("batchPurSaleBool", 0);	
				theQueryPurchaseBatch.setParameter("batchVariantId", batch.getBatchVariantId());
				
				EntityBatchesOrders dbPurchaseBatch = (EntityBatchesOrders) theQueryPurchaseBatch.getSingleResult();
				
				dbPurchaseBatch.setCurrentQuantity(dbPurchaseBatch.getCurrentQuantity() - batch.getQuantity());
				entityManager.merge(dbPurchaseBatch);
				
				Query theQueryVariant = currentSession.createQuery("from EntityProductVariantByUnitOrders where variantId =:variantId");
				theQueryVariant.setParameter("variantId", batch.getBatchVariantId());
				
				EntityProductVariantByUnitOrders dbVariant = (EntityProductVariantByUnitOrders) theQueryVariant.getSingleResult();
				
				dbVariant.setQuantity(dbVariant.getQuantity() - batch.getQuantity());
				entityManager.merge(dbVariant);
			}			
		}			
	}

}
