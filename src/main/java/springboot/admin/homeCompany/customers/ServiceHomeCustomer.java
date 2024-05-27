package springboot.admin.homeCompany.customers;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ServiceHomeCustomer {
	
	private RepositoryCustomersHome repositoryCustomer;
    private EntityManager entityManager;
    private DAOHomeCustomer dAOCustomers;


    @Autowired
    public ServiceHomeCustomer(EntityManager entityManager, RepositoryCustomersHome repositoryCustomer, DAOHomeCustomer dAOCustomers) {
     //   super();
        this.entityManager = entityManager;
        this.repositoryCustomer = repositoryCustomer;
        this.dAOCustomers = dAOCustomers;
    }

    @Transactional
    public ResponseCustomersHome searchCustomer (String searchTerm, String customerType, Integer itemsPerPage, Integer startIndex) { 

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        
        try {
			fullTextEntityManager.createIndexer().startAndWait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(EntityCustomerHome.class).get(); 
        
        List<EntityCustomerHome> dbCustomers = new ArrayList<EntityCustomerHome>();
        ResponseCustomersHome responseCustomers = new ResponseCustomersHome();
        
        if(customerType.equals("b2c")) {
        	Query luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1).onFields("fullName")
                .matching(searchTerm).createQuery();
        	
        	javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, EntityCustomerHome.class);
        	
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
        	javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, EntityCustomerHome.class);
        	
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
        	EntityCustomerHome customerItr = (EntityCustomerHome) itr.next();
        	if(customerItr.getCustomerDeleteStatus() != null && customerItr.getCustomerDeleteStatus().equals(true)) {
        		itr.remove();
        	}
        } 
        
        Iterator itr2 = dbCustomers.iterator();
        while(itr2.hasNext()) {
        	EntityCustomerHome customerItr = (EntityCustomerHome) itr2.next();
        	
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
	public EntityCustomerHome save(EntityCustomerHome customer) {
		
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
	public EntityCustomerHome editCustomer(EntityCustomerHome customer) throws SQLException {
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
    		
    		return dAOCustomers.editCustomer(customer);
    		
    		//return repositoryCustomer.save(customer);
    	}
    	
    	else return null;
	}

    @Transactional
	public ResponseCustomersHome getCustomers(String customerType, Integer itemsPerPage, Integer startIndex) {
		return dAOCustomers.getCustomers(customerType, itemsPerPage, startIndex);
	}

	public EntityCustomerHome findById(Integer customerId) {
		// TODO Auto-generated method stub
		return dAOCustomers.findById(customerId);
	}

	
	@Transactional
	public Boolean deleteById(Integer customerId) {
		
		return dAOCustomers.deleteById(customerId);
	}
	
	
	@Transactional
	public void resetPassword(DTOPasswordResetHome dTOPasswordReset) {
			
	    dAOCustomers.resetPassword(dTOPasswordReset);
	}

	@Transactional
	public List<EntityCustomerHome> searchCustomerByPhoneNo(String customerType, String phoneNo) {
		
		return dAOCustomers.searchCustomerByPhoneNo(customerType, phoneNo);
	}


}