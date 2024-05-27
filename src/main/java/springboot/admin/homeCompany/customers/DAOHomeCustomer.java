package springboot.admin.homeCompany.customers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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

import springboot.properties.TenantDbProperties;


@Repository
public class DAOHomeCustomer {

	private EntityManager entityManager;
	private String tenantDbUrl;
	private String tenantDbUsername;
	private String tenantDbPassword;
	
	@Autowired
	private RepositoryCustomersHome repositoryCustomer;
	
	//set up constructor injection	
	public DAOHomeCustomer() {	
	}
		
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	public DAOHomeCustomer(EntityManager theEntityManager, TenantDbProperties tenantDbProperties) {	
		this.entityManager = theEntityManager;
		this.tenantDbUrl = tenantDbProperties.getTenantsDbUrl();
		this.tenantDbUsername = tenantDbProperties.getTenantDbUsername();
		this.tenantDbPassword = tenantDbProperties.getTenantDbPassword();
	}
	

	 
	public ResponseCustomersHome getCustomers(String customerType, Integer itemsPerPage, Integer startIndex) {
		
		ResponseCustomersHome responseCustomers = new ResponseCustomersHome();
		
		Session currentSession = entityManager.unwrap(Session.class);		
		Query theQuery = currentSession.createQuery("from EntityCustomerHome where (customerDeleteStatus is NULL OR customerDeleteStatus=:deleteStatus) "
												+ " AND (customerType is NULL OR customerType=:customerType)  ORDER BY customerId DESC");	
		theQuery.setParameter("deleteStatus", false);
		theQuery.setParameter("customerType", customerType);
		
		theQuery.setFirstResult(startIndex);
		theQuery.setMaxResults(itemsPerPage); 
		
		if(startIndex == 0) {
			Query countQuery = currentSession.createQuery("SELECT count(c.customerId) FROM EntityCustomerHome c" );		
			Long countResults = (Long) countQuery.uniqueResult();
			responseCustomers.setCountOfCustomers(countResults);
		}
		
		List<EntityCustomerHome> dbCustomers = theQuery.getResultList();
		responseCustomers.setCustomers(dbCustomers);
		
		return responseCustomers;
	}
	
	
	public EntityCustomerHome findById(Integer customerId) {
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		EntityCustomerHome theCustomer = currentSession.get(EntityCustomerHome.class, customerId);
				
		return theCustomer;
	}
	
	

	public EntityCustomerHome editCustomer(EntityCustomerHome customer) throws SQLException {
		
		Session currentSession = entityManager.unwrap(Session.class);	
		
		Query theQuery = currentSession.createQuery("from EntityCustomerHome where customerId=:customerId");	
		theQuery.setParameter("customerId", customer.getCustomerId());
		
		EntityCustomerHome dbCustomerOld = (EntityCustomerHome) theQuery.getSingleResult();
		
		customer.setTenants(dbCustomerOld.getTenants());
		
		EntityCustomerHome dbCustomer = repositoryCustomer.save(customer);
		
		//System.out.println();
		
		if(customer.getTenants() != null) {
		
			for(EntityTenantCustomer tenant : customer.getTenants()) {
				
	        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/" + tenant.getTenantUrl() + "?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
	    		String username =  tenantDbUsername;
	    		String password = tenantDbPassword;
				
		        try(
			    		Connection connection = DriverManager.getConnection(url, username, password);			    		
			            Statement statement = connection.createStatement();		
		        ) {       	
		            String updateCustomerDetails = "UPDATE customers set first_name = \"" + customer.getFirstName() + "\"" 
		            												+ ", last_name = \"" + customer.getLastName() + "\""
		            												+ ", full_name = \"" + customer.getFullName() + "\""
		            												+ ", company_name = \"" + customer.getCompanyName() + "\""
		            												+ ", phone_no_1 = \"" + customer.getPhoneNo1() + "\""
		            												+ ", phone_no_2 = \"" + customer.getPhoneNo2() + "\""
		            												+ ", email = \"" + customer.getEmail() + "\""
		            												+ ", address = \"" + customer.getAddress() + "\""
		            												+ ", postal_code = \"" + customer.getPostalCode() + "\""
		            												+ ", city = \"" + customer.getCity() + "\""
		            												+ ", state = \"" + customer.getState() + "\""
		            												+ " where customer_home_id = " + customer.getCustomerId();
		            statement.executeUpdate(updateCustomerDetails);
		        } 
		        catch (SQLException e) {
		        	//e.printStackTrace();
		        	throw e;
		        }
			}
		}
		return dbCustomer;
	}


	 
	public void resetPassword(DTOPasswordResetHome dTOPasswordReset) {
		
		Session currentSession = entityManager.unwrap(Session.class);		
		EntityCustomerPasswordHome dbCustomer = currentSession.get(EntityCustomerPasswordHome.class, dTOPasswordReset.getCustomerId());	
		
		dbCustomer.setPassword(encoder.encode(dTOPasswordReset.getNewPassword()));
		currentSession.saveOrUpdate(dbCustomer);
	}


	 
	public List<EntityCustomerHome> searchCustomerByPhoneNo(String customerType, String phoneNo) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query theQuery = currentSession.createQuery("from EntityCustomerHome where (phoneNo1=:phoneNo1 OR phoneNo2=:phoneNo2)");
		theQuery.setParameter("phoneNo1",phoneNo);
		theQuery.setParameter("phoneNo2",phoneNo);
	//	theQuery.setParameter("customerType", customerType);
		
		List<EntityCustomerHome> dbCustomers = theQuery.getResultList();
		
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
		
		return dbCustomers;
	}
	
	 
	public Boolean deleteById(Integer customerId) {
		
		
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query theQueryOrders = currentSession.createQuery("select count(o.orderId) from EntityOrderCustomer o JOIN o.customer c where c.customerId=:customerId");
		theQueryOrders.setParameter("customerId",customerId);
		
		Long batchesCount =  (Long) theQueryOrders.uniqueResult();
		
		if(batchesCount != 0) {
			return false;
		}
		else {
			Query theQuery = currentSession.createQuery("from EntityCustomerHome where customerId=:customerId");
			theQuery.setParameter("customerId",customerId);
			
			List<EntityCustomerHome> dbCustomers = theQuery.getResultList();
			
			for(EntityCustomerHome customer : dbCustomers) {
				customer.setCustomerDeleteStatus(true);
				currentSession.saveOrUpdate(customer);
				currentSession.flush();
			}
			return true;
		}
		
	}



	  

}
