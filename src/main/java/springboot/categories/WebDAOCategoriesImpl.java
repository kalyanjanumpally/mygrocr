package springboot.categories;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class WebDAOCategoriesImpl implements WebDAOCategories {

	private EntityManager entityManager;
	
	//set up constructor injection	
	public WebDAOCategoriesImpl() {	
	}
	
	@Autowired
	public WebDAOCategoriesImpl(EntityManager theEntityManager) {	
		this.entityManager = theEntityManager;
	}
	
	@Override
	public List<WebEntityCategory> findALL() {
	
		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);	
		
		//create a query
		Query<WebEntityCategory> theQuery = currentSession.createQuery("from WebEntityCategory", WebEntityCategory.class);
		
		//execute the query and get result list
		List<WebEntityCategory> webEntityCategories = theQuery.getResultList();
		
		//return result	
		return webEntityCategories;
	}

	@Override
	public WebEntityCategory findById(int theId) {
		
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		//get the employee
		WebEntityCategory theCategory = currentSession.get(WebEntityCategory.class, theId);
		
		//return the employee		
		return theCategory;
	}
	
	

	@Override
	public WebEntityCategory save(WebEntityCategory theCategory) {
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		currentSession.saveOrUpdate(theCategory);
		
		Query theQuery = currentSession.createQuery("from WebEntityCategory where categoryName=:CategoryName");
		theQuery.setParameter("CategoryName",theCategory.getCategoryName());	
		List<WebEntityCategory> returnCategories = theQuery.getResultList();	
		return returnCategories.get(0);	
	}

	@Override
	public void deleteById(int theId) {
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query theQuery = currentSession.createQuery("delete from WebEntityCategory where categoryId=:CategoryId");
		theQuery.setParameter("CategoryId",theId);
		
		theQuery.executeUpdate();
		
	}

	@Override
	public List<WebEntityCategory> findByParentCategoryId(Integer parentCategoryId) {
		
		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);	
		
		//create a query
	//	Query<Category> theQuery = currentSession.createQuery("select from Category where parentCategoryId=:parentCatId", Category.class);
		
		Query<WebEntityCategory> theQuery = currentSession.createQuery("from WebEntityCategory C where C.parentCategoryId=:parentCatId", WebEntityCategory.class);
						
		theQuery.setParameter("parentCatId", parentCategoryId);
			
		//execute the query and get result list
		List<WebEntityCategory> WebEntityCategories = theQuery.getResultList();
		
		//return result	
		return WebEntityCategories;
	}

}
