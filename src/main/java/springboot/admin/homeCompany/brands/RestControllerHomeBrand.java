package springboot.admin.homeCompany.brands;

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
public class RestControllerHomeBrand {
	
	//quick: inject product DAO (use Constructor injection)

	private ServiceHomeBrand serviceHomeBrand;
	
	@Autowired
	public RestControllerHomeBrand(ServiceHomeBrand theServiceHomeBrand) {
		serviceHomeBrand = theServiceHomeBrand;
	}
	
	// expose "/brands" and return list of brands
	
	@GetMapping("/get-brands")
	public List<EntityBrandHome> findAll() {
		
		return serviceHomeBrand.findAll();
	}
	
	@PostMapping("/add-new-brand")
	public void addNewBrand(@RequestBody EntityBrandHome brand) throws Exception {
		
		serviceHomeBrand.addNewBrand(brand);		
	}
	
	@PutMapping("/edit-brand")
	public void editBrand(@RequestBody EntityBrandHome brand) throws Exception {
		
		serviceHomeBrand.editBrand(brand);		
	}
	
	@DeleteMapping("/delete-brand/{brandId}")
	public Boolean deleteBrand(@PathVariable Integer brandId) throws Exception {
		EntityBrandHome theBrand = serviceHomeBrand.findById(brandId);
		
		if(theBrand == null) {
			throw new RuntimeException("Brand id not found: " + brandId);
		}		
			return serviceHomeBrand.deleteById(brandId);		
	}

}







