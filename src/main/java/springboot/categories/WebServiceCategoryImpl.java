package springboot.categories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class WebServiceCategoryImpl implements WebServiceCategory {

	private WebDAOCategories webDAOCategories;
	
	@Autowired
	public WebServiceCategoryImpl(WebDAOCategories theCategoriesDAO) {		
		webDAOCategories = theCategoriesDAO;
	}
	
	@Override
	@Transactional
	public List<WebEntityCategory> findAll() {			
		return webDAOCategories.findALL();
	}

	@Override
	@Transactional
	public WebEntityCategory findById(int theId) {		
		return webDAOCategories.findById(theId);
	}

	@Override
	@Transactional
	public WebEntityCategory save(WebEntityCategory theCategory) {
		return webDAOCategories.save(theCategory);
	}

	@Override
	@Transactional
	public void deleteById(int theId) {
		webDAOCategories.deleteById(theId);
	}
	
	@Override
	@Transactional
	public List<WebEntityCategory> findByParentCategoryId(Integer parentCategoryId) {
		return webDAOCategories.findByParentCategoryId(parentCategoryId);
	}
	
	@Transactional
	public List<WebEntityCategory> findByParentCategoryId(int parentCategoryId) {
		return webDAOCategories.findByParentCategoryId(parentCategoryId);
	}

}
