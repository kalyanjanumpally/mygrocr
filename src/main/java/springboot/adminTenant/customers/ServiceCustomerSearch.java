package springboot.adminTenant.customers;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import springboot.adminTenant.product.EntityProduct;
import springboot.adminTenant.product.ResponseProducts;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ServiceCustomerSearch {
	
	private RepositoryCustomers repositoryCustomer;
    private EntityManager entityManager;
    private DAOCustomers dAOCustomers;


    @Autowired
    public ServiceCustomerSearch(EntityManager entityManager, RepositoryCustomers repositoryCustomer, DAOCustomers dAOCustomers) {
     //   super();
        this.entityManager = entityManager;
        this.repositoryCustomer = repositoryCustomer;
        this.dAOCustomers = dAOCustomers;
    }

    @Transactional
    public ResponseCustomers searchCustomer (String searchTerm, String customerType, Integer itemsPerPage, Integer startIndex) { 

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        
        try {
			fullTextEntityManager.createIndexer().startAndWait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(EntityCustomer.class).get();
        
  
        List<EntityCustomer> dbCustomers = new ArrayList<EntityCustomer>();
        ResponseCustomers responseCustomers = new ResponseCustomers();
        
        if(customerType.equals("b2c")) {
        	Query luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1).onFields("fullName")
                .matching(searchTerm).createQuery();
        	
        	javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, EntityCustomer.class);
        	
    		jpaQuery.setFirstResult(startIndex);
    		jpaQuery.setMaxResults(itemsPerPage);
    		
            try {
                dbCustomers = jpaQuery.getResultList();
            } catch (NoResultException nre) {
                ;// do nothing
            }
     		if(startIndex == 0) {
     			Integer countResults = ((FullTextQuery) jpaQuery).getResultSize();
     			responseCustomers.setCountOfCustomers( Long.valueOf(countResults));
     		}
        }
        else if(customerType.equals("b2b")) {
        	Query luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1).onFields("companyName")
                    .matching(searchTerm).createQuery(); 
        	javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, EntityCustomer.class);
        	
    		jpaQuery.setFirstResult(startIndex);
    		jpaQuery.setMaxResults(itemsPerPage);
    		
            try {
                dbCustomers = jpaQuery.getResultList();
            } catch (NoResultException nre) {
                ;// do nothing
            }
            
     		if(startIndex == 0) {
     			Integer countResults = ((FullTextQuery) jpaQuery).getResultSize();
     			responseCustomers.setCountOfCustomers( Long.valueOf(countResults));
     		}
        }
   
        // remove soft deleted customers and irrelevant customer types 
        Iterator itr = dbCustomers.iterator();
        
        while(itr.hasNext()) {
        	EntityCustomer customerItr = (EntityCustomer) itr.next();
        	if(customerItr.getCustomerDeleteStatus() != null && customerItr.getCustomerDeleteStatus().equals(true)) {
        		itr.remove();
        	}
        } 
        
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
        
 		responseCustomers.setCustomers(dbCustomers);

        return responseCustomers;
    }

    @Transactional
	public EntityCustomer save(EntityCustomer customer) {
		
    	Boolean validationSuccess = true;
    	
    	Pattern phoneNoPattern = Pattern.compile("^\\d{10}$");
    	Pattern emailPattern = Pattern.compile("^(.+)@(\\S+)$");
    	
    	Boolean phone1ValidationSuccess = phoneNoPattern.matcher(customer.getPhoneNo1()).matches();
    	
    	Boolean phone2ValidationSuccess = true;
    	if(!customer.getPhoneNo2().isEmpty()) {
    		phone2ValidationSuccess = phoneNoPattern.matcher(customer.getPhoneNo2()).matches();   		
    	}
	
    	Boolean emailValidationSuccess = true;
    	if(!customer.getEmail().isEmpty()) {
    		emailValidationSuccess = emailPattern.matcher(customer.getEmail()).matches();   		
    	}
    	
    	validationSuccess = phone1ValidationSuccess && phone2ValidationSuccess && emailValidationSuccess;
    	
    	if(validationSuccess == true) {   		
    		if ((!customer.getEmail().isEmpty() &&   repositoryCustomer.existsByEmail(customer.getEmail()))  || repositoryCustomer.existsByPhoneNo1(customer.getPhoneNo1()) ) {
    			
    			return null;
    		} 		
    		return repositoryCustomer.save(customer);
    	}
    	
    	else return null;
		
	}
    
    @Transactional
	public EntityCustomer editCustomer(EntityCustomer customer) {
    	Boolean validationSuccess = true;
    	
    	Pattern phoneNoPattern = Pattern.compile("^\\d{10}$");
    	Pattern emailPattern = Pattern.compile("^(.+)@(\\S+)$");
    	
    	Boolean phone1ValidationSuccess = phoneNoPattern.matcher(customer.getPhoneNo1()).matches();
    	
    	Boolean phone2ValidationSuccess = true;
    	if(!customer.getPhoneNo2().isEmpty()) {
    		phone2ValidationSuccess = phoneNoPattern.matcher(customer.getPhoneNo2()).matches();   		
    	}
	
    	Boolean emailValidationSuccess = true;
    	if(!customer.getEmail().isEmpty()) {
    		emailValidationSuccess = emailPattern.matcher(customer.getEmail()).matches();   		
    	}
    	
    	validationSuccess = phone1ValidationSuccess && phone2ValidationSuccess && emailValidationSuccess;
    	
    	if(validationSuccess == true) {
    		return repositoryCustomer.save(customer);
    	}
    	
    	else return null;
	}

    @Transactional
	public ResponseCustomers getCustomers(String customerType, Integer itemsPerPage, Integer startIndex) {
		return dAOCustomers.getCustomers(customerType, itemsPerPage, startIndex);
	}

	public EntityCustomer findById(Integer customerId) {
		// TODO Auto-generated method stub
		return dAOCustomers.findById(customerId);
	}

	@Transactional
	public Boolean deleteById(Integer customerId) {
		
		return dAOCustomers.deleteById(customerId);
	}
	
	/*
	@Transactional
	public void resetPassword(DTOPasswordReset dTOPasswordReset) {
			
	    dAOCustomers.resetPassword(dTOPasswordReset);
	}
	*/

	@Transactional
	public List<EntityCustomer> searchCustomerByPhoneNo(String customerType, String phoneNo) {
		
		return dAOCustomers.searchCustomerByPhoneNo(customerType, phoneNo);
	}


}