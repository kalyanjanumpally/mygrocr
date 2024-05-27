package springboot.admin.homeCompany.images;

import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.search.jpa.FullTextQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DAOImageHome {
	
	private EntityManager entityManager;
	
	public DAOImageHome() {		
	}
	
	@Autowired
	public DAOImageHome(EntityManager theEntityManager) {
		entityManager = theEntityManager;
	}

	public EntityImageHome save(EntityImageHome EntityImageHome) {
		// TODO Auto-generated method stub
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query theQuery = currentSession.createQuery("from EntityImageHome where imageName=:imageName");
		theQuery.setParameter("imageName",EntityImageHome.getImageName());
		
		List<EntityImageHome> images = theQuery.getResultList();
		
		if (images.size() ==0) {
			return entityManager.merge(EntityImageHome);
		}

		return null;
	}

	public ResponseImagesHome findAll(Integer itemsPerPage, Integer startIndex) {
		
		ResponseImagesHome responseImages = new ResponseImagesHome();
		
		Session currentSession = entityManager.unwrap(Session.class);		
		Query theQuery = currentSession.createQuery("from EntityImageHome ORDER BY imageId DESC");		
		
		theQuery.setFirstResult(startIndex);
		theQuery.setMaxResults(itemsPerPage);
		
		if(startIndex == 0) {			
			Query countQuery = currentSession.createQuery("select count(i.imageId) from EntityImageHome i");
			Long countResults =  (Long) countQuery.uniqueResult();

			responseImages.setCountOfImages(countResults);
		}
				
		List<EntityImageHome> dbImages = theQuery.getResultList();		
		responseImages.setImages(dbImages);	
		
		return responseImages;
	}

	
	
	
}
