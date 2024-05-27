package springboot.admin.homeCompany.categories;

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
@RequestMapping("/api/admin")
public class RestControllerHomeCategory {
	
	//quick: inject employee DAO (use Constructor injection)

	private ServiceHomeCategory serviceHomeCategory;
	
	@Autowired
	public RestControllerHomeCategory(ServiceHomeCategory theCategoryService) {
		serviceHomeCategory = theCategoryService;
	}
	
	// expose "/categories" and return list of categories
	
	@GetMapping("/get-categories")
	public List<EntityCategoryHome> findAll() {
		
		return serviceHomeCategory.findAll();
	}

	//add mapping for GET/ categories/ {categoryId}
	
	@GetMapping("/categories/{categoryId}")
	public EntityCategoryHome getCategory(@PathVariable int categoryId) {
					
		EntityCategoryHome theCategory = serviceHomeCategory.findById(categoryId);
		
		if(theCategory == null) {
			
			throw new RuntimeException("Category id not found: " + categoryId );
		} 
		else {
			return theCategory;
		}
		
	}
	
	//add mapping for POST /categories - add new category
	
 /*	@PostMapping("/categories")
	public Category addCategory(@RequestBody Category theCategory) {		
		theCategory.setCategoryId(0);	
		categoryService.save(theCategory);	
		return theCategory;
	} */
	
	@PostMapping("/add-new-category")
	public EntityCategoryHome addCategory(@RequestBody EntityCategoryHome theCategory) throws Exception {		
		theCategory.setCategoryId(0);	
		EntityCategoryHome returnCategory = serviceHomeCategory.save(theCategory);	
		return returnCategory;
	}
	
	//add mapping for PUT /categories - update existing category
	
	@PutMapping("/edit-category")
	public EntityCategoryHome updateCategory(@RequestBody EntityCategoryHome theCategory) throws Exception {
		
		serviceHomeCategory.updateCategory(theCategory);
		
		return theCategory;
	}
	
	//add mapping for DELETE /categories/{categoryId}	
	@DeleteMapping("/delete-category/{categoryId}")
	public Boolean deleteCategory(@PathVariable Integer categoryId) throws Exception {
		
		EntityCategoryHome theCategory = serviceHomeCategory.findById(categoryId);
		
		if(theCategory == null) {
			throw new RuntimeException("Category id not found: " + categoryId);
		}		
		return	serviceHomeCategory.deleteById(categoryId);		
	//	return "Deleted Category Id: " + categoryId;
		
	}
	
	//add mapping for GET/ categories/ {parentCategoryId}
	
	@GetMapping("/childcategories/{parentCategoryId}")
	public List<EntityCategoryHome> getChildCategories(@PathVariable int parentCategoryId) {

		List<EntityCategoryHome> theCategories = serviceHomeCategory.findByParentCategoryId(parentCategoryId);

		if (theCategories == null) {

			throw new RuntimeException("Child Categories not found for parent Category: " + parentCategoryId);
		} else {
			return theCategories;
		}
	}	
}







