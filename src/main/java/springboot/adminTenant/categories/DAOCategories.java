package springboot.adminTenant.categories;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DAOCategories  {

	private EntityManager entityManager;
	
	//set up constructor injection	
	public DAOCategories() {	
	}
	
	@Autowired
	public DAOCategories(EntityManager theEntityManager) {	
		this.entityManager = theEntityManager;
	}
	
	public List<EntityCategory> findALL() {
	
		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);	
		
		//create a query
		Query<EntityCategory> theQuery = currentSession.createQuery("from EntityCategory", EntityCategory.class);
		
		//execute the query and get result list
		List<EntityCategory> entityCategories = theQuery.getResultList();
		
		//return result	
		return entityCategories;
	}

/*	
	public EntityCategory findById(int theId) {
		
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		//get the employee
		EntityCategory theCategory = currentSession.get(EntityCategory.class, theId);
		
		//return the employee		
		return theCategory;
	}
	

	public List<EntityCategory> findByParentCategoryId(int parentCategoryId) {
		
		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);	
		
		//create a query
		Query<EntityCategory> theQuery = currentSession.createQuery("from EntityCategory where parentCategoryId=:parentCatId", EntityCategory.class);
				
		theQuery.setParameter("parentCatId", parentCategoryId);
		
		//execute the query and get result list
		List<EntityCategory> EntityCategories = theQuery.getResultList();
		
		//return result	
		return EntityCategories;
	}
*/
}
