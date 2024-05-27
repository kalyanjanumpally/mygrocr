package springboot.categories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@CrossOrigin
@RestController
@RequestMapping("/api")
public class WebRestControllerCategory {
	
	//quick: inject employee DAO (use Constructor injection)

	private WebServiceCategory webServiceCategory;
	
	@Autowired
	public WebRestControllerCategory(WebServiceCategory theCategoryService) {
		webServiceCategory = theCategoryService;
	}
	
	// expose "/categories" and return list of categories
	
	@GetMapping("/categories")
	public List<WebEntityCategory> findAll() {
		
		return webServiceCategory.findAll();
	}

	//add mapping for GET/ categories/ {categoryId}
	
	@GetMapping("/categories/{categoryId}")
	public WebEntityCategory getCategory(@PathVariable int categoryId) {
					
		WebEntityCategory theCategory = webServiceCategory.findById(categoryId);
		
		if(theCategory == null) {
			
			throw new RuntimeException("Category id not found: " + categoryId );
		} 
		else {
			return theCategory;
		}
		
	}
	
	
	
	@GetMapping("/categories-in-category/{parentCategoryId}")
	public List<WebEntityCategory> getChildCategories(@PathVariable int parentCategoryId) {

		List<WebEntityCategory> theCategories = webServiceCategory.findByParentCategoryId(parentCategoryId);

		if (theCategories == null) {

			throw new RuntimeException("Child Categories not found for parent Category: " + parentCategoryId);
		} else {
			return theCategories;
		}
	}
	
	
	//add mapping for POST /categories - add new category
	
 /*	@PostMapping("/categories")
	public Category addCategory(@RequestBody Category theCategory) {		
		theCategory.setCategoryId(0);	
		categoryService.save(theCategory);	
		return theCategory;
	} */
	
	@PostMapping("/categories")
	public WebEntityCategory addCategory(@RequestBody WebEntityCategory theCategory) {		
		theCategory.setCategoryId(0);	
		WebEntityCategory returnCategory = webServiceCategory.save(theCategory);	
		return returnCategory;
	}
	
	//add mapping for PUT /categories - update existing category
	
	@PutMapping("/categories")
	public WebEntityCategory updateProduct(@RequestBody WebEntityCategory theCategory) {
		
		webServiceCategory.save(theCategory);
		
		return theCategory;
	}
	
	//add mapping for DELETE /categories/{categoryId}	
	@DeleteMapping("/categories/{categoryId}")
	public String deleteCategory(@PathVariable int categoryId) {
		
		WebEntityCategory theCategory = webServiceCategory.findById(categoryId);
		
		if(theCategory == null) {
			throw new RuntimeException("Category id not found: " + categoryId);
		}		
			webServiceCategory.deleteById(categoryId);		
		return "Deleted Category Id: " + categoryId;
		
	}
	
	//add mapping for GET/ categories/ {parentCategoryId}
	
	@GetMapping("/child-categories/{parentCategoryId}")
	public List<WebEntityCategory> getChildCategories(@PathVariable Integer parentCategoryId) {

		List<WebEntityCategory> theCategories = webServiceCategory.findByParentCategoryId(parentCategoryId);

		if (theCategories == null) {

			throw new RuntimeException("Child Categories not found for parent Category: " + parentCategoryId);
		} else {
			return theCategories;
		}
	}	
}







