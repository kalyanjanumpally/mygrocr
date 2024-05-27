package springboot.adminTenant.brands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import springboot.adminTenant.categories.EntityCategory;
import springboot.adminTenant.product.EntityProduct;


@Repository
public class DAOEntityBrand  {

	private EntityManager entityManager;
	
	//set up constructor injection
	
	public DAOEntityBrand() {	
	}
	
	@Autowired
	public DAOEntityBrand(EntityManager theEntityManager) {
		
		this.entityManager = theEntityManager;
	}
	

	public List<EntityBrand> findALL() {
	
		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		//create a query
		Query<EntityBrand> theQuery = currentSession.createQuery("from EntityBrand b order by b.brandId", EntityBrand.class);
		
		//execute the query and get result list
		List<EntityBrand> brands = theQuery.getResultList();
		
		//return result	
		return brands;
	}

/*
	public void addNewBrand(EntityBrand brand) {
		
		entityManager.merge(brand);
		
	}
	

	public void editBrand(EntityBrand brand) {
		entityManager.merge(brand);		
	}


	public EntityBrand findById(Integer brandId) {
		
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		//get the employee
		EntityBrand theBrand = currentSession.get(EntityBrand.class, brandId);
		
		//return the employee		
		return theBrand;
	}


	public Boolean deleteById(Integer brandId) {
		
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query theQueryProducts = currentSession.createQuery("select count(p) from EntityProductBrand p where p.brand.brandId =:brandId");
		theQueryProducts.setParameter("brandId",brandId);
		
		Long productsCount =  (Long) theQueryProducts.uniqueResult();
		
		if(productsCount != 0) {
			return false;
		}
		else {
			Query theQuery = currentSession.createQuery("delete from EntityBrand where brandId=:brandId");
			theQuery.setParameter("brandId", brandId);
			
			theQuery.executeUpdate();
			return true;
		}
				
	}
*/

}
