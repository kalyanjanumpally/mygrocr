package springboot.adminTenant.suppliers;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DAOSuppliers {

	private EntityManager entityManager;
	
	//set up constructor injection	
	public DAOSuppliers() {	
	}
	
	@Autowired
	public DAOSuppliers(EntityManager theEntityManager) {	
		this.entityManager = theEntityManager;
	}
	
	
	public List<EntitySupplier> findALL() {
	
		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);	
		
		//create a query
		Query<EntitySupplier> theQuery = currentSession.createQuery("from EntitySupplier", EntitySupplier.class);
		
		//execute the query and get result list
		List<EntitySupplier> entitySuppliers = theQuery.getResultList();
		
		//return result	
		return entitySuppliers;
	}

	
	public EntitySupplier findById(int theId) {
		
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		//get the employee
		EntitySupplier theSupplier = currentSession.get(EntitySupplier.class, theId);
		
		//return the employee		
		return theSupplier;
	}

	
	public EntitySupplier save(EntitySupplier theSupplier) {
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		currentSession.saveOrUpdate(theSupplier);
		
		Query theQuery = currentSession.createQuery("from EntitySupplier where supplierName=:supplierName");
		theQuery.setParameter("supplierName",theSupplier.getSupplierName());	
		List<EntitySupplier> returnSuppliers = theQuery.getResultList();	
		return returnSuppliers.get(0);	
	}

	
	public void deleteById(int theId) {
		//get current Hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query theQuery = currentSession.createQuery("delete from EntitySupplier where supplierId=:supplierId");
		theQuery.setParameter("supplierId",theId);
		
		theQuery.executeUpdate();
		
	}

}
