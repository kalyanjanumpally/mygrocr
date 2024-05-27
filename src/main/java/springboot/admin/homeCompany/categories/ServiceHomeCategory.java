package springboot.admin.homeCompany.categories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ServiceHomeCategory {

	private DAOHomeCategories dAOCategories;
	
	@Autowired
	public ServiceHomeCategory(DAOHomeCategories theCategoriesDAO) {		
		dAOCategories = theCategoriesDAO;
	}
	
	@Transactional
	public List<EntityCategoryHome> findAll() {			
		return dAOCategories.findAll();
	}

	@Transactional
	public EntityCategoryHome findById(int theId) {		
		return dAOCategories.findById(theId);
	}

	@Transactional
	public EntityCategoryHome save(EntityCategoryHome theCategory) throws Exception {
		return dAOCategories.save(theCategory);
	}
	
	@Transactional
	public EntityCategoryHome updateCategory(EntityCategoryHome theCategory) throws Exception {
		return dAOCategories.updateCategory(theCategory);
		
	}

	@Transactional
	public Boolean deleteById(Integer theId) throws Exception {
		return dAOCategories.deleteById(theId);
	}
	
	@Transactional
	public List<EntityCategoryHome> findByParentCategoryId(int parentCategoryId) {
		return dAOCategories.findByParentCategoryId(parentCategoryId);
	}

}
