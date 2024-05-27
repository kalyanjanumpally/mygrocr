package springboot.adminTenant.batches;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import springboot.adminTenant.orders.EntityProductVariantByUnitOrders;
import springboot.adminTenant.purchase.EntityBatchesPurchase;


//import springboot.admin.orders.EntityBrandOrders;

@Repository
public class DAOBatches {

	private EntityManager entityManager;
	
	//set up constructor injection	
	public DAOBatches() {	
	}
	
	@Autowired
	public DAOBatches(EntityManager theEntityManager) {	
		this.entityManager = theEntityManager;
	}

	
	public List<DTOVariantBatches> findBatches(Integer variantId, Integer itemsPerPage, Integer startIndex) {
		
		Query theQuery = entityManager.createQuery("select b, p.productName, p.brand, o.dateTimeCreated, pur.date, v.unit from EntityBatch b "
				+ " LEFT OUTER JOIN EntityProductBatches p ON b.batchProductId = p.productId "
				+ " LEFT OUTER JOIN EntityProductVariantByUnitBatches v ON b.batchVariantId = v.variantId "
				+ " LEFT OUTER JOIN EntityOrderBatches o ON b.batchOrderId = o.orderId "
				+ " LEFT OUTER JOIN EntityPurchaseInvoiceBatches pur ON b.batchPurchaseInvoiceId = pur.purchaseInvoiceId "
				+ " WHERE b.batchVariantId=:batchVariantId AND (b.transactionStatus IS NULL OR b.transactionStatus NOT IN (:transactionStatus)) "
				+ " ORDER BY b.batchId DESC");
		theQuery.setParameter("batchVariantId",variantId);
		theQuery.setParameter("transactionStatus", Arrays.asList(new String[]{"order-returned", "batch-deleted", "order-cancelled"}));
		
	//	List<EntityBatch> batches = theQuery.getResultList(); 
		theQuery.setFirstResult(startIndex);
		theQuery.setMaxResults(itemsPerPage); 
		
		List<Object[]> dbBatches = theQuery.getResultList();
		
		List<DTOVariantBatches> batches = new ArrayList<DTOVariantBatches>();
		
		for(Object[] obj : dbBatches) {
			
			DTOVariantBatches batch = new DTOVariantBatches();
			
	    	batch.setBatchId( ((EntityBatch) obj[0]).getBatchId() );
	    	batch.setBatchProductId( ((EntityBatch) obj[0]).getBatchProductId() );
	    	batch.setBatchVariantId( ((EntityBatch) obj[0]).getBatchVariantId() );
	    	batch.setBatchProductHomeId( ((EntityBatch) obj[0]).getBatchProductHomeId() );
	    	batch.setBatchVariantHomeId( ((EntityBatch) obj[0]).getBatchVariantHomeId() );		    	
			batch.setBatchProductName(((EntityBatch) obj[0]).getBatchProductName());
			batch.setBatchUnit(((EntityBatch) obj[0]).getBatchUnit());
			batch.setBatchBrandName(((EntityBatch) obj[0]).getBatchBrandName());	    	
	    	batch.setBatchNo( ((EntityBatch) obj[0]).getBatchNo() );
	    	batch.setBatchPurSaleBool( ((EntityBatch) obj[0]).getBatchPurSaleBool() );
	    	batch.setDisplay( ((EntityBatch) obj[0]).getDisplay() );
	    	batch.setQuantity( ((EntityBatch) obj[0]).getQuantity() );
	    	batch.setMrp( ((EntityBatch) obj[0]).getMrp() );
	    	batch.setSellingPrice( ((EntityBatch) obj[0]).getSellingPrice() );
	    	batch.setBatchPurchasePrice( ((EntityBatch) obj[0]).getBatchPurchasePrice() );
	    	batch.setPpIncludesGST( ((EntityBatch) obj[0]).getPpIncludesGST() );
	    	batch.setExpiryDate( ((EntityBatch) obj[0]).getExpiryDate() );
	    	batch.setBatchOrderId( ((EntityBatch) obj[0]).getBatchOrderId() );
	    	batch.setTransactionStatus( ((EntityBatch) obj[0]).getTransactionStatus() );	  //  	batch.setUnit((String) obj[5]);
	    	
	   // 	batch.setBatchProductName((String)obj[1]);
	  //  	batch.setBatchProductBrand(( (EntityBrandBatches) obj[2]).getBrandName() );
	    	
	    	if(obj[3] != null) {	    		
	    		java.util.Date tempDate = (java.util.Date) obj[3];		    		
	    		batch.setDate(new Date(tempDate.getTime()));
	    	}
	    	else if(obj[4] != null) {
	    		batch.setDate((Date) obj[4]);
	    	}
	    	else {
	    		batch.setDate( ((EntityBatch)obj[0]).getBatchEntryDate());
	    	}
	    	
	    	batches.add(batch);						
		}
		
		//	Collections.sort(batches, DtoBatchAndProductBatches.BatchesComparator);
		
		 return batches; 		
	}
	
	
	public Long countOfAllBatches(Integer variantId) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query countQuery =
				  currentSession.createQuery("select count(b.batchId) from EntityBatch b where b.batchProductId=:batchProductId "
				  		+ "AND (b.transactionStatus IS NULL OR b.transactionStatus NOT IN (:transactionStatus))");
		countQuery.setParameter("batchProductId", variantId);	
		countQuery.setParameter("transactionStatus", Arrays.asList(new String[]{"order-returned", "batch-deleted", "order-cancelled"}));
		
		Long countResults =  (Long) ((org.hibernate.query.Query) countQuery).uniqueResult();
			  
	    //return result 
		return countResults; 
	}
	
	
	public Long countOfPurchaseBatches(Integer variantId) {
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query countQuery =
				  currentSession.createQuery("select count(b.batchId) from EntityBatch b where b.batchVariantId=:batchVariantId "
				  		+ "AND (b.transactionStatus IS NULL OR b.transactionStatus NOT IN (:transactionStatus)) AND "
				  		+ " b.batchPurSaleBool = 0 ");
		countQuery.setParameter("batchVariantId", variantId);	
		countQuery.setParameter("transactionStatus", Arrays.asList(new String[]{"order-returned", "batch-deleted", "order-cancelled"}));
		
		Long countResults =  (Long) ((org.hibernate.query.Query) countQuery).uniqueResult();
			  
	    //return result 
		return countResults; 
	}

	
	public List<DTOVariantBatches> getPurchaseBatches(Integer variantId, Integer startIndex, Integer itemsPerPage) {
		
		Query theQuery = entityManager.createQuery("select b, p.productName, p.brand, pur.date from EntityBatch b "
				+ " LEFT OUTER JOIN EntityProductBatches p ON b.batchProductId = p.productId "
				+ " LEFT OUTER JOIN EntityPurchaseInvoiceBatches pur ON b.batchPurchaseInvoiceId = pur.purchaseInvoiceId "
				+ " WHERE b.batchVariantId=:batchVariantId AND b.batchPurSaleBool=:batchPurSaleBool AND "
				+ " (b.transactionStatus is NULL OR b.transactionStatus !=:transactionStatus) "
				+ " ORDER BY pur.purchaseInvoiceId DESC");
		theQuery.setParameter("batchVariantId",variantId);
		theQuery.setParameter("batchPurSaleBool",0);
		theQuery.setParameter("transactionStatus", "batch-returned");
		
		theQuery.setFirstResult(startIndex);
		theQuery.setMaxResults(itemsPerPage); 
		
		List<Object[]> dbBatches = theQuery.getResultList();
		
		List<DTOVariantBatches> batches = new ArrayList<DTOVariantBatches>();
		
		for(Object[] obj : dbBatches) {
			
			DTOVariantBatches batch = new DTOVariantBatches();
			
	    	batch.setBatchId( ((EntityBatch) obj[0]).getBatchId() );
	    	batch.setBatchProductId( ((EntityBatch) obj[0]).getBatchProductId() );
	    	batch.setBatchVariantId( ((EntityBatch) obj[0]).getBatchVariantId() );	    	
	    	batch.setBatchProductHomeId( ((EntityBatch) obj[0]).getBatchProductHomeId() );
	    	batch.setBatchVariantHomeId( ((EntityBatch) obj[0]).getBatchVariantHomeId() );	    	
			batch.setBatchProductName(((EntityBatch) obj[0]).getBatchProductName());
			batch.setBatchUnit(((EntityBatch) obj[0]).getBatchUnit());
			batch.setBatchBrandName(((EntityBatch) obj[0]).getBatchBrandName());	    	
	    	batch.setBatchNo( ((EntityBatch) obj[0]).getBatchNo() );
	    	batch.setBatchPurSaleBool( ((EntityBatch) obj[0]).getBatchPurSaleBool() );
	    	batch.setDisplay( ((EntityBatch) obj[0]).getDisplay() );
	    	batch.setCurrentQuantity( ((EntityBatch) obj[0]).getCurrentQuantity() );
	    	batch.setQuantity( ((EntityBatch) obj[0]).getQuantity() );
	    	batch.setMrp( ((EntityBatch) obj[0]).getMrp() );
	    	batch.setSellingPrice( ((EntityBatch) obj[0]).getSellingPrice() );
	    	batch.setBatchPurchasePrice( ((EntityBatch) obj[0]).getBatchPurchasePrice() );
	    	batch.setPpIncludesGST( ((EntityBatch) obj[0]).getPpIncludesGST() );
	    	batch.setExpiryDate( ((EntityBatch) obj[0]).getExpiryDate() );
	    	batch.setBatchOrderId( ((EntityBatch) obj[0]).getBatchOrderId() );
	    	batch.setTransactionStatus( ((EntityBatch) obj[0]).getTransactionStatus() );
	    	
	 //   	batch.setBatchProductName((String)obj[1]);
	//    	batch.setBatchProductBrand(( (EntityBrandBatches) obj[2]).getBrandName() );
	    	batch.setDate((Date) obj[3]);
	    	
	    	
	    	batches.add(batch);						
		}
		
		//	Collections.sort(batches, DtoBatchAndProductBatches.BatchesComparator);
		
		 return batches; 	
	} 

	
	public void batchDisplayChange(String batchNo, Integer variantId) {
		
		Query theQuery = entityManager.createQuery("from EntityBatch where batchNo=:batchNo AND batchVariantId=:variantId AND batchPurSaleBool=:batchPurSaleBool ");
		theQuery.setParameter("batchNo",batchNo);
		theQuery.setParameter("batchPurSaleBool",0);
		theQuery.setParameter("variantId",variantId);
		
		EntityBatch dbBatch = (EntityBatch) theQuery.getSingleResult();
		
		dbBatch.setDisplay(!dbBatch.getDisplay());
		entityManager.merge(dbBatch);
		
		//update the quantity of the variant..
		
		Query theQueryVariant = entityManager.createQuery("from EntityProductVariantByUnitBatches where variantId =:variantId");
		theQueryVariant.setParameter("variantId",variantId);
		
		EntityProductVariantByUnitBatches dbVariant = (EntityProductVariantByUnitBatches) theQueryVariant.getSingleResult();
		
		if(dbBatch.getDisplay()) {
			dbVariant.setQuantity(dbVariant.getQuantity() + dbBatch.getCurrentQuantity());
			entityManager.merge(dbVariant);
		}
		else {
			dbVariant.setQuantity(dbVariant.getQuantity() - dbBatch.getCurrentQuantity());
			entityManager.merge(dbVariant);			
		}
		
	}

	
	public void updateStock(DTOStockUpdate batchUpdate) {
			
		EntityBatch batch = new EntityBatch();
		
		batch.setBatchNo(batchUpdate.getBatchNo());
		batch.setBatchProductId(batchUpdate.getBatchProductId());
		batch.setBatchVariantId(batchUpdate.getBatchVariantId());
		batch.setBatchProductHomeId(batchUpdate.getBatchProductHomeId());
		batch.setBatchVariantHomeId(batchUpdate.getBatchVariantHomeId());			
		batch.setBatchProductName(batchUpdate.getBatchProductName());
		batch.setBatchUnit(batchUpdate.getBatchUnit());
		batch.setBatchBrandName(batchUpdate.getBatchBrandName());			
		batch.setQuantity(batchUpdate.getQuantity());
		batch.setTransactionStatus(batchUpdate.getTransactionStatus());	
		batch.setBatchEntryDate(batchUpdate.getBatchEntryDate());
		batch.setBatchId((long) 0);
		
		entityManager.merge(batch);
		
		//now, update the quantity in the variant-
		
		Query theQuery = entityManager.createQuery("from EntityProductVariantByUnitBatches where variantId=:variantId");
		
		theQuery.setParameter("variantId",batch.getBatchVariantId());
		
		EntityProductVariantByUnitBatches variant = (EntityProductVariantByUnitBatches) theQuery.getSingleResult();
		
		variant.setQuantity(variant.getQuantity() + batch.getQuantity());
		
		entityManager.merge(variant);
		
		//now, update the quantity in the purchase entry of batch

		Query theQueryPurchaseBatch = entityManager.createQuery("from EntityBatch where batchNo=:batchNo AND batchVariantId=:batchVariantId AND batchPurSaleBool=:batchPurSaleBool");
		theQueryPurchaseBatch.setParameter("batchNo",batch.getBatchNo());
		theQueryPurchaseBatch.setParameter("batchPurSaleBool",0);
		theQueryPurchaseBatch.setParameter("batchVariantId", batch.getBatchVariantId());
		
		EntityBatch dbPurchaseBatch = (EntityBatch) theQueryPurchaseBatch.getSingleResult();
		
		dbPurchaseBatch.setCurrentQuantity(dbPurchaseBatch.getCurrentQuantity() + batch.getQuantity());
		entityManager.merge(dbPurchaseBatch);
		
	
	}
	
	
	public void updateSellingPriceOfBatch(String batchNo, Integer variantId, Float sellingPrice) {
		
		Query theQuery = entityManager.createQuery("from EntityBatch where batchNo=:batchNo AND batchVariantId=:variantId AND batchPurSaleBool=:batchPurSaleBool ");
		
		theQuery.setParameter("batchNo",batchNo);
		theQuery.setParameter("batchPurSaleBool",0);
		theQuery.setParameter("variantId", variantId);
		
		EntityBatch dbBatch = (EntityBatch) theQuery.getSingleResult();
		
		if(sellingPrice != 0) {
			dbBatch.setSellingPrice(sellingPrice);
		}
		else {
			dbBatch.setSellingPrice(null);
		}
		
		entityManager.merge(dbBatch);
		
	}
	
	
	public void updateMrpOfBatch(String batchNo, Integer variantId, Float mrp) {
		
		Query theQuery = entityManager.createQuery("from EntityBatch where batchNo=:batchNo AND batchVariantId=:variantId AND batchPurSaleBool=:batchPurSaleBool ");
		
		theQuery.setParameter("batchNo",batchNo);
		theQuery.setParameter("batchPurSaleBool",0);
		theQuery.setParameter("variantId", variantId);
		
		EntityBatch dbBatch = (EntityBatch) theQuery.getSingleResult();
		
		if(mrp != 0) {
			dbBatch.setMrp(mrp);
		}
		
		entityManager.merge(dbBatch);
		
	}

	
	public List<EntityBatch> findExpiryStockInDatesRange(java.util.Date fromDate, java.util.Date toDate) {
		
		Query theQuery = entityManager.createQuery("from EntityBatch b WHERE b.expiryDate BETWEEN :fromDate AND :toDate AND batchPurSaleBool=:batchPurSaleBool AND currentQuantity > 0");
		theQuery.setParameter("fromDate",fromDate);
		theQuery.setParameter("toDate", toDate);
		theQuery.setParameter("batchPurSaleBool", 0);
	
		List<EntityBatch> dbBatches = theQuery.getResultList();
		
		return dbBatches;
	}
	
	
	
	public List<DTONonPerishablesOrder> downloadNonPerishablesOrder(Integer brandId) {
		
		long millisToday = System.currentTimeMillis(); 	
		long millis1Day = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);
		long millis30Days = TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS);		
		long millis31Days = TimeUnit.MILLISECONDS.convert(31, TimeUnit.DAYS);
		long millis60Days = TimeUnit.MILLISECONDS.convert(60, TimeUnit.DAYS);

		
		java.sql.Date dateLast30DaysFrom = new java.sql.Date(millisToday - millis30Days);	    
		java.sql.Date dateLast30DaysTo = new java.sql.Date(millisToday - millis1Day);
		java.sql.Date dateLastToLast30DaysFrom = new java.sql.Date(millisToday - millis60Days);
		java.sql.Date dateLastToLast30DaysTo = new java.sql.Date(millisToday - millis31Days);

		/*
		Query theQueryLast30Days = entityManager.createQuery("select p.productName, b.quantity, b.transactionStatus from EntityBatch b "
				+ " LEFT OUTER JOIN EntityProductBatches p ON b.batchProductId = p.productId"
				+ " LEFT OUTER JOIN EntityOrderBatches o ON b.batchOrderId  = o.orderId"); 
				+ " WHERE o.dateTimeCreated BETWEEN :fromDate AND :toDate AND p.brand.brandId =:brandId " 
				+ "AND b.batchPurSaleBool = 1"); 
		theQueryLast30Days.setParameter("fromDate",dateLastToLast30DaysFrom);
	    theQueryLast30Days.setParameter("toDate", dateLastToLast30DaysTo);
		theQueryLast30Days.setParameter("brandId",brandId );
	
		List<Object[]> dbRawBatchesLast30Days = theQueryLast30Days.getResultList();
		
		System.out.println("output batches:");
		System.out.println(dbRawBatchesLast30Days.size());
		
		Query theQueryLastToLast30Days = entityManager.createQuery("select b, p.productName from EntityBatch b "
				+ " LEFT OUTER JOIN EntityProductBatches p ON b.batchProductId = p.productId LEFT OUTER JOIN EntityOrderBatches o ON b.batchOrderId  = o.orderId"
				+ " WHERE o.dateTimeCreated BETWEEN :fromDate AND :toDate AND p.brand.brandId =:brandId");
		theQueryLastToLast30Days.setParameter("fromDate",dateLastToLast30DaysFrom);
		theQueryLastToLast30Days.setParameter("toDate", dateLastToLast30DaysTo);
		theQueryLastToLast30Days.setParameter("brandId",brandId );
	
		List<Object[]> dbRawBatchesLastToLast30Days = theQueryLastToLast30Days.getResultList();
		*/
		



	//	return theQuery.list();	
		return null;
	}





	 
}
