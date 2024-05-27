package springboot.adminTenant.categories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ServiceCategory {

	private DAOCategories dAOCategories;
	
	@Autowired
	public ServiceCategory(DAOCategories theCategoriesDAO) {		
		dAOCategories = theCategoriesDAO;
	}
	

	@Transactional
	public List<EntityCategory> findAll() {			
		return dAOCategories.findALL();
	}

/*
	@Transactional
	public EntityCategory findById(int theId) {		
		return dAOCategories.findById(theId);
	}
	

	@Transactional
	public List<EntityCategory> findByParentCategoryId(int parentCategoryId) {
		return dAOCategories.findByParentCategoryId(parentCategoryId);
	}
*/
}
