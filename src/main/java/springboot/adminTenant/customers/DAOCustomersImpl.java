package springboot.adminTenant.customers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import springboot.adminTenant.product.EntityProduct;

@Repository
public class DAOCustomersImpl implements DAOCustomers {

	private EntityManager entityManager;
	
	//set up constructor injection	
	public DAOCustomersImpl() {	
	}
		
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	public DAOCustomersImpl(EntityManager theEntityManager) {	
		this.entityManager = theEntityManager;
	}
	

	@Override
	public ResponseCustomers getCustomers(String customerType, Integer itemsPerPage, Integer startIndex) {
		
		ResponseCustomers responseCustomers = new ResponseCustomers();
		
		Session currentSession = entityManager.unwrap(Session.class);		
		Query theQuery = currentSession.createQuery("from EntityCustomer where (customerDeleteStatus is NULL OR customerDeleteStatus=:deleteStatus) "
												+ " AND (customerType is NULL OR customerType=:customerType)  ORDER BY customerId DESC");	
		theQuery.setParameter("deleteStatus", false);
		theQuery.setParameter("customerType", customerType);
		
		theQuery.setFirstResult(startIndex);
		theQuery.setMaxResults(itemsPerPage); 
		
		if(startIndex == 0) {
			Query countQuery = currentSession.createQuery("SELECT count(c.customerId) FROM EntityCustomer c" );		
			Long countResults = (Long) countQuery.uniqueResult();
			responseCustomers.setCountOfCustomers(countResults);
		}
		
		List<EntityCustomer> dbCustomers = theQuery.getResultList();
		responseCustomers.setCustomers(dbCustomers);
		
		return responseCustomers;
	}

	@Override
	public EntityCustomer findById(Integer customerId) {
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		EntityCustomer theCustomer = currentSession.get(EntityCustomer.class, customerId);
				
		return theCustomer;
	}

	@Override
	public Boolean deleteById(Integer customerId) {
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query theQueryCustomers = currentSession.createQuery("select count(o.orderId) from EntityOrderCustomer o JOIN o.customer c where c.customerId=:customerId");
		theQueryCustomers.setParameter("customerId",customerId);
		
		Long count =  (Long) theQueryCustomers.uniqueResult();
		
		if(count != 0) {
			return false;
		}
		else {
			Query theQuery = currentSession.createQuery("from EntityCustomer where customerId=:customerId");
			theQuery.setParameter("customerId",customerId);
			
			List<EntityCustomer> dbCustomers = theQuery.getResultList();
			
			for(EntityCustomer customer : dbCustomers) {
				customer.setCustomerDeleteStatus(true);
				currentSession.saveOrUpdate(customer);
				currentSession.flush();
			}
			return true;
		}
	}

	/*
	@Override
	public void resetPassword(DTOPasswordReset dTOPasswordReset) {
		
		Session currentSession = entityManager.unwrap(Session.class);		
		EntityCustomerPassword dbCustomer = currentSession.get(EntityCustomerPassword.class, dTOPasswordReset.getCustomerId());	
		
		dbCustomer.setPassword(encoder.encode(dTOPasswordReset.getNewPassword()));
		currentSession.saveOrUpdate(dbCustomer);
	}
	*/


	@Override
	public List<EntityCustomer> searchCustomerByPhoneNo(String customerType, String phoneNo) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query theQuery = currentSession.createQuery("from EntityCustomer where (phoneNo1=:phoneNo1 OR phoneNo2=:phoneNo2)");
		theQuery.setParameter("phoneNo1",phoneNo);
		theQuery.setParameter("phoneNo2",phoneNo);
	//	theQuery.setParameter("customerType", customerType);
		
		List<EntityCustomer> dbCustomers = theQuery.getResultList();
		
        Iterator itr2 = dbCustomers.iterator();
        while(itr2.hasNext()) {
        	EntityCustomer customerItr = (EntityCustomer) itr2.next();
        	
        	if(customerType.equals("b2c")) {        	
        		if( !(customerItr.getCustomerType() == null || customerItr.getCustomerType().equals("b2c")) ) {
        			itr2.remove();
        		}
        	}
        	else if (customerType.equals("b2b")) {
        		if( (customerItr.getCustomerType() == null || customerItr.getCustomerType().equals("b2c")) ) {
        			itr2.remove();
        		}
        	}
        } 
		
		return dbCustomers;
	}	  

}
