package springboot.adminTenant.purchase;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import springboot.adminTenant.orders.EntityCustomerPaymentsOrders;
import springboot.adminTenant.orders.EntityOrder;

@Repository
public class DAOPurchase {

	private EntityManager entityManager;
	
	//set up constructor injection	
	public DAOPurchase() {	
	}
	
	@Autowired
	public DAOPurchase(EntityManager theEntityManager) {	
		this.entityManager = theEntityManager;
	}

	
	public EntityPurchaseInvoice save(EntityPurchaseInvoice thePurchaseInvoice) {
			
	//	Session currentSession = entityManager.unwrap(Session.class);		
		return  entityManager.merge(thePurchaseInvoice);
	}

	
	public EntityPurchaseInvoice findById(Long purchaseInvoiceId) {
		Session currentSession = entityManager.unwrap(Session.class);		
		return currentSession.get(EntityPurchaseInvoice.class, purchaseInvoiceId); 
	}

	
	public List<EntityPurchaseInvoice> getPurchaseInvoiceAndReturn(Long purchaseInvoiceId) {
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query theQuery =
				  currentSession.createQuery("from EntityPurchaseInvoice where purchaseInvoiceId=:purchaseInvoiceId OR purReturnId=:purchaseReturnId");	  
		theQuery.setParameter("purchaseInvoiceId", purchaseInvoiceId);
		theQuery.setParameter("purchaseReturnId", purchaseInvoiceId);
		
		//execute the query and get result list 
		List<EntityPurchaseInvoice> dbPurchases = theQuery.getResultList();
				  
	    //return result 
		return dbPurchases;
	}

	
	public EntityPurchaseInvoice savePurchaseReturn(EntityPurchaseInvoice purchase) {
		purchase.setPurchaseDeliveryStatus("Purchase Return");
		return entityManager.merge(purchase);
	}

	
	public List<EntityPurchaseInvoice> findAllPurchases(Integer itemsPerPage, Integer startIndex) {
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query theQuery = currentSession.createQuery("from EntityPurchaseInvoice ORDER BY purchaseInvoiceId DESC");	
		
		theQuery.setFirstResult(startIndex);
		theQuery.setMaxResults(itemsPerPage); 
				
		//execute the query and get result list 
		List<EntityPurchaseInvoice> dbPurchases = theQuery.getResultList();
				  
	    //return result 
		return dbPurchases; 
	}

	
	public Long countOfAllPurchases() {
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query countQuery = currentSession.createQuery("select count(o.purchaseInvoiceId) from EntityPurchaseInvoice o");	 		
		Long countResults =  (Long) countQuery.uniqueResult();
				  
	    //return result 
		return countResults; 
	}

	
	public void updatePurchase(EntityPurchaseInvoice purchase) {
		//	Session currentSession = entityManager.unwrap(Session.class);
		
		EntityPurchaseInvoice dbPurchase = entityManager.find(EntityPurchaseInvoice.class, purchase.getPurchaseInvoiceId()); 
		
//		long millis = System.currentTimeMillis(); 
//		java.sql.Date todayDate = new java.sql.Date(millis);
		
		dbPurchase.setAmount(purchase.getAmount());
		entityManager.merge(dbPurchase);
		
	}


	public EntityPurchaseInvoice findInitialStockPurchaseInvoice(Date fromDate, Date toDate) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		Query theQuery = currentSession.createQuery("from EntityPurchaseInvoice WHERE (dateCreated BETWEEN :fromDate AND :toDate) "
				+ " AND supplier.supplierName=:supplierName  ORDER BY purchaseInvoiceId DESC");	
		
		theQuery.setParameter("fromDate", fromDate);
		theQuery.setParameter("toDate", toDate);
		theQuery.setParameter("supplierName", "Initial Stock");
		theQuery.setFirstResult(0);
		theQuery.setMaxResults(1); 
				
		//execute the query and get result list 
		List<EntityPurchaseInvoice> dbPurchases = theQuery.getResultList();
		 
	    //return result 
		if(dbPurchases.size() == 0) {
			return null;
		}
		else {
			return dbPurchases.get(0); 
		}
	}
	 

}
