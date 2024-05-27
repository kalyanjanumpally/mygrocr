package springboot.adminTenant.categories;

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
@RequestMapping("/api/admin-tenant")
public class RestControllerCategory {
	
	//quick: inject employee DAO (use Constructor injection)

	private ServiceCategory serviceCategory;
	
	@Autowired
	public RestControllerCategory(ServiceCategory theCategoryService) {
		serviceCategory = theCategoryService;
	}
	
	// expose "/categories" and return list of categories
	
	@GetMapping("/categories")
	public List<EntityCategory> findAll() {
		
		return serviceCategory.findAll();
	}
	

	//add mapping for GET/ categories/ {categoryId}
	/*
	@GetMapping("/categories/{categoryId}")
	public EntityCategory getCategory(@PathVariable int categoryId) {
					
		EntityCategory theCategory = serviceCategory.findById(categoryId);
		
		if(theCategory == null) {
			
			throw new RuntimeException("Category id not found: " + categoryId );
		} 
		else {
			return theCategory;
		}
		
	}
	*/
	
	//add mapping for GET/ categories/ {parentCategoryId}
	/*
	@GetMapping("/categories-in-category/{parentCategoryId}")
	public List<EntityCategory> getChildCategories(@PathVariable int parentCategoryId) {

		List<EntityCategory> theCategories = serviceCategory.findByParentCategoryId(parentCategoryId);

		if (theCategories == null) {

			throw new RuntimeException("Child Categories not found for parent Category: " + parentCategoryId);
		} else {
			return theCategories;
		}
	}
	*/
		
}







