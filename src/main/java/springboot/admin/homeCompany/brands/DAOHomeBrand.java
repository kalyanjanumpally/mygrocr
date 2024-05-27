package springboot.admin.homeCompany.brands;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import springboot.properties.TenantDbProperties;


@Repository
public class DAOHomeBrand {

	private EntityManager entityManager;
	private String tenantDbUrl;
	private String tenantDbUsername;
	private String tenantDbPassword;
	
	//set up constructor injection
	
	public DAOHomeBrand() {	
	}
	
	@Autowired
	public DAOHomeBrand(EntityManager theEntityManager, TenantDbProperties tenantDbProperties) {
		
		this.entityManager = theEntityManager;
		this.tenantDbUrl = tenantDbProperties.getTenantsDbUrl();
		this.tenantDbUsername = tenantDbProperties.getTenantDbUsername();
		this.tenantDbPassword = tenantDbProperties.getTenantDbPassword();
		
	}
	
	public List<EntityBrandHome> findAll() {
	
		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		//create a query
		Query<EntityBrandHome> theQuery = currentSession.createQuery("from EntityBrandHome b order by b.brandId", EntityBrandHome.class);
		
		//execute the query and get result list
		List<EntityBrandHome> brands = theQuery.getResultList();
		
		//return result	
		return brands;
	}

	public void addNewBrand(EntityBrandHome brand) throws Exception {
		
		entityManager.merge(brand);
		entityManager.flush();
		
		Query theQuery = (Query) entityManager.createQuery("from EntityBrandHome where brandName=:brandName");
		theQuery.setParameter("brandName",brand.getBrandName());		
		
		EntityBrandHome returnBrand = (EntityBrandHome) theQuery.getSingleResult();
		
		
		Query queryTenant = (Query) entityManager.createQuery("from EntityTenantBrand");
		
		List<EntityTenantBrand> dbTenants =  queryTenant.getResultList();
		
		for(EntityTenantBrand tenant : dbTenants) {
			
        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/" + tenant.getTenantUrl() +  "?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
    		String username = tenantDbUsername;
    		String password = tenantDbPassword;
		
	       try(
	    		  Connection connection = DriverManager.getConnection(url, username, password);
	    		  Statement statement = connection.createStatement();	    		   
	    		   
	       ) {       	
		        String addNewBrandInTenantDb = "INSERT INTO brands(brand_name, brand_home_id) VALUES ( \" " + brand.getBrandName() + " \", " +   returnBrand.getBrandId() + ");"; 	            		        
	            statement.executeUpdate(addNewBrandInTenantDb);
	       }
		   catch(Exception e) {
			//	e.printStackTrace();
			   throw e;
		   }     
		}
		
		
		
	}
	
	public void editBrand(EntityBrandHome brand) throws Exception {
		entityManager.merge(brand);	
		entityManager.flush();		
		
		/*
		Query theQuery = (Query) entityManager.createQuery("from EntityBrandHome where brandId=:brandId");
		theQuery.setParameter("brandId", brand.getBrandId());	
		//List<EntityCategoryHome> returnCategories = theQuery.getResultList();
		
		EntityBrandHome returnBrand = (EntityBrandHome) theQuery.getSingleResult();
		*/
		
		Query queryTenant = (Query) entityManager.createQuery("from EntityTenantBrand");
		
		List<EntityTenantBrand> dbTenants =  queryTenant.getResultList();		
		
		for(EntityTenantBrand tenant : dbTenants) {
			
        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/" + tenant.getTenantUrl() +  "?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
    		String username = tenantDbUsername;
    		String password = tenantDbPassword;
		
 	       try(
 	    		  Connection connection = DriverManager.getConnection(url, username, password);
 	    		  Statement statement = connection.createStatement();	    		   
 	    		   
 	       ) {           
		        String updateBrandInTenantDb = "UPDATE brands SET brand_name = \" " + brand.getBrandName() + " \" WHERE brand_home_id = " + brand.getBrandId() + ";"; 	            
		        
	            statement.executeUpdate(updateBrandInTenantDb);
	       }
		   catch(Exception e) {
				//e.printStackTrace();
			   throw e;
		   }     
		}
		
		
	}

	public EntityBrandHome findById(Integer brandId) {
		
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		//get the employee
		EntityBrandHome theBrand = currentSession.get(EntityBrandHome.class, brandId);
		
		//return the employee		
		return theBrand;
	}

	public Boolean deleteById(Integer brandId) throws Exception {
		
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query theQueryProducts = currentSession.createQuery("select count(p) from EntityProductBrandHome p where p.brand.brandId =:brandId");
		theQueryProducts.setParameter("brandId",brandId);
		
		Long productsCount =  (Long) theQueryProducts.uniqueResult();
		
		if(productsCount != 0) {
			return false;
		}
		else {
			Query theQuery = currentSession.createQuery("delete from EntityBrandHome where brandId=:brandId");
			theQuery.setParameter("brandId", brandId);
			
			theQuery.executeUpdate();
			
	
			
			Query queryTenant = currentSession.createQuery("from EntityTenantBrand");
			
			List<EntityTenantBrand> dbTenants =  queryTenant.getResultList();
			
			for(EntityTenantBrand tenant : dbTenants) {
			
		       	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/" + tenant.getTenantUrl() +  "?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
	    		String username = tenantDbUsername;
	    		String password = tenantDbPassword;
			
	 	       try(
	 	    		  Connection connection = DriverManager.getConnection(url, username, password);
	 	    		  Statement statement = connection.createStatement();	    		   
	 	    		   
	 	       ) {
		            
			        String deleteBrandInTenantDb = "DELETE FROM brands where brand_home_id = " + brandId + ";"; 	            
			        
		            statement.executeUpdate(deleteBrandInTenantDb);
		       }
			   catch(Exception e) {
					//e.printStackTrace();
				   throw e;
			   }     
			}
				
			return true;
		}
				
	}


}
