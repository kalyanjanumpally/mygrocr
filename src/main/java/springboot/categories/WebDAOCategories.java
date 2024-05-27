package springboot.categories;

import java.util.List;


public interface WebDAOCategories {
	
	public List<WebEntityCategory> findALL();
	
	public WebEntityCategory findById(int theId);
	
	public WebEntityCategory save(WebEntityCategory theCategory);
	
	public void deleteById(int theId);

	public List<WebEntityCategory> findByParentCategoryId(Integer parentCategoryId);

}
