package springboot.admin.homeCompany.categories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import springboot.properties.TenantDbProperties;

@Repository
public class DAOHomeCategories {

	private EntityManager entityManager;
	private String tenantDbUrl;
	private String tenantDbUsername;
	private String tenantDbPassword;
	
	
	public DAOHomeCategories() {	
	}
	
	@Autowired
	public DAOHomeCategories(EntityManager theEntityManager, TenantDbProperties tenantDbProperties) {	
		this.entityManager = theEntityManager;
		this.tenantDbUrl = tenantDbProperties.getTenantsDbUrl();
		this.tenantDbUsername = tenantDbProperties.getTenantDbUsername();
		this.tenantDbPassword = tenantDbProperties.getTenantDbPassword();
	}
	
	public List<EntityCategoryHome> findAll() {
	
		Session currentSession = entityManager.unwrap(Session.class);	
		
		Query<EntityCategoryHome> theQuery = currentSession.createQuery("from EntityCategoryHome", EntityCategoryHome.class);
		
		List<EntityCategoryHome> entityCategories = theQuery.getResultList();
		
		return entityCategories;
	}

	public EntityCategoryHome findById(int theId) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		EntityCategoryHome theCategory = currentSession.get(EntityCategoryHome.class, theId);
			
		return theCategory;
	}

	public EntityCategoryHome save(EntityCategoryHome theCategory) throws Exception {
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		currentSession.save(theCategory);
		currentSession.flush();
		
		Query theQuery = currentSession.createQuery("from EntityCategoryHome where categoryName=:CategoryName");
		theQuery.setParameter("CategoryName",theCategory.getCategoryName());		
		
		EntityCategoryHome returnCategory = (EntityCategoryHome) theQuery.getSingleResult();
		
		
		Query queryTenant = currentSession.createQuery("from EntityTenantCategory");
		
		List<EntityTenantCategory> dbTenants =  queryTenant.getResultList();
		
		for(EntityTenantCategory tenant : dbTenants) {
		
        	String url ="jdbc:mysql://" + tenantDbUrl + ":3306/" + tenant.getTenantUrl() +  "?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
    		String username = tenantDbUsername;
    		String password = tenantDbPassword;
			
	       try(
	    		   Connection connection = DriverManager.getConnection(url, username, password);
	    		   Statement statement = connection.createStatement()
	       ) {       		    		            
		        String addNewCategoryInTenantDb = "INSERT INTO categories(category_name, category_home_id, parent_category_id) VALUES ( \" " + theCategory.getCategoryName() + " \", " +   returnCategory.getCategoryId() + ", " + theCategory.getParentCategoryId()  + ");"; 	            		        
	            statement.executeUpdate(addNewCategoryInTenantDb);
	       }
		   catch(Exception e) {
			   throw e;
		   }     
		}

		return returnCategory;
	}
	
	public EntityCategoryHome updateCategory(EntityCategoryHome theCategory) throws Exception {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		currentSession.saveOrUpdate(theCategory);
		currentSession.flush();
		
		Query theQuery = currentSession.createQuery("from EntityCategoryHome where categoryId=:CategoryId");
		theQuery.setParameter("CategoryId",theCategory.getCategoryId());	
		
		EntityCategoryHome returnCategory = (EntityCategoryHome) theQuery.getSingleResult();
		
		
		Query queryTenant = currentSession.createQuery("from EntityTenantCategory");
		
		List<EntityTenantCategory> dbTenants =  queryTenant.getResultList();
		
		for(EntityTenantCategory tenant : dbTenants) {
		
        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/" + tenant.getTenantUrl() +  "?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
    		String username = tenantDbUsername;
    		String password = tenantDbPassword;
			
	        try(	    		
	        		Connection connection = DriverManager.getConnection(url, username, password);	    		
		            Statement statement = connection.createStatement();	
	        ) {       	
		        String updateCategoryInTenantDb = "UPDATE categories SET category_name = \" " + theCategory.getCategoryName() + " \" , parent_category_id = " + theCategory.getParentCategoryId()  + " WHERE category_home_id = " + theCategory.getCategoryId() + ";"; 	            		        
	            statement.executeUpdate(updateCategoryInTenantDb);
	       }
		   catch(Exception e) {
			 throw e; 
		   }     
		}
		
		return returnCategory;
	}

	public Boolean deleteById(Integer theId) throws Exception {

		Session currentSession = entityManager.unwrap(Session.class);
		
		EntityCategoryHome theCategory = currentSession.get(EntityCategoryHome.class, theId);	
		
		 Query theQueryCategories = currentSession.createQuery("from EntityCategoryHome");
		 
		 List<EntityCategoryHome> dbCategories = theQueryCategories.getResultList();
		 
		 Integer childCategoriesCount = 0;		
		 
		 for(EntityCategoryHome category : dbCategories) {
			 
			 if(category.getParentCategoryId() == theId) {				 
				 childCategoriesCount = childCategoriesCount + 1;
			 }
		 }
		
		if(theCategory.getProducts().size() == 0 && childCategoriesCount == 0) {
			
			
			Query theQuery = currentSession.createQuery("delete from EntityCategoryHome where categoryId=:CategoryId");
			theQuery.setParameter("CategoryId",theId);
			
			theQuery.executeUpdate();
			
			Query queryTenant = currentSession.createQuery("from EntityTenantCategory");
						
			List<EntityTenantCategory> dbTenants =  queryTenant.getResultList();
			
			for(EntityTenantCategory tenant : dbTenants) {
				
	        	String url = "jdbc:mysql://" + tenantDbUrl + ":3306/" + tenant.getTenantUrl() +  "?useSSL=false&serverTimezone=UTC&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&allowPublicKeyRetrieval=true";
	    		String username = tenantDbUsername;
	    		String password = tenantDbPassword;
			
		        try(	    		
		        		Connection connection = DriverManager.getConnection(url, username, password);	    		
			            Statement statement = connection.createStatement();	
		        ) {	            
			        String deleteCategoryInTenantDb = "DELETE FROM categories where category_home_id = " + theId + ";"; 	                
		            statement.executeUpdate(deleteCategoryInTenantDb);
		        }
			    catch(Exception e) {
				    throw e;
			    }     
			}
			
			return true;			
		}
		else {			
			return false;
		}		
	}

	public List<EntityCategoryHome> findByParentCategoryId(int parentCategoryId) {
		
		Session currentSession = entityManager.unwrap(Session.class);	
		
		Query<EntityCategoryHome> theQuery = currentSession.createQuery("from Category C where C.parentCategoryId=:parentCatId", EntityCategoryHome.class);
						
		theQuery.setParameter("parentCatId", parentCategoryId);
	
		List<EntityCategoryHome> EntityCategories = theQuery.getResultList();

		return EntityCategories;
	}


}
