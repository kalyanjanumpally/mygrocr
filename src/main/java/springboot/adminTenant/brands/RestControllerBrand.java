package springboot.adminTenant.brands;

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

import springboot.adminTenant.categories.EntityCategory;


@CrossOrigin 
@RestController
@RequestMapping("/api/admin-tenant")
public class RestControllerBrand {
	
	//quick: inject product DAO (use Constructor injection)

	private ServiceBrand serviceBrand;
	
	@Autowired
	public RestControllerBrand(ServiceBrand theServiceBrand) {
		serviceBrand = theServiceBrand;
	}
	
	// expose "/brands" and return list of brands
	
	@GetMapping("/brands")
	public List<EntityBrand> findAll() {
		
		return serviceBrand.findAll();
	}
	
	/*
	@PostMapping("/add-new-brand")
	public void addNewBrand(@RequestBody EntityBrand brand) {
		
		System.out.println("brand id: " + brand.getBrandId());
		System.out.println("brand: " + brand.getBrandName());
		
		serviceBrand.addNewBrand(brand);		
	}
	
	@PutMapping("/edit-brand")
	public void editBrand(@RequestBody EntityBrand brand) {
		
		serviceBrand.editBrand(brand);		
	}
	
	@DeleteMapping("/delete-brand/{brandId}")
	public Boolean deleteBrand(@PathVariable Integer brandId) {
		EntityBrand theBrand = serviceBrand.findById(brandId);
		
		if(theBrand == null) {
			throw new RuntimeException("Category id not found: " + brandId);
		}		
			return serviceBrand.deleteById(brandId);		
	}
	*/

}







