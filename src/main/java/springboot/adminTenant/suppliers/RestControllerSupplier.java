package springboot.adminTenant.suppliers;

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
public class RestControllerSupplier {
	
	//quick: inject employee DAO (use Constructor injection)

	private ServiceSupplier serviceSupplier;
	
	@Autowired
	public RestControllerSupplier(ServiceSupplier theSupplierService) {
		serviceSupplier = theSupplierService;
	}
	
	// expose "/suppliers" and return list of suppliers
	
	@GetMapping("/suppliers")
	public List<EntitySupplier> findAll() {
		
		return serviceSupplier.findAll();
	}

	//add mapping for GET/ suppliers/ {supplierId}
	
	@GetMapping("/suppliers/{supplierId}")
	public EntitySupplier getSupplier(@PathVariable int supplierId) {
					
		EntitySupplier theSupplier = serviceSupplier.findById(supplierId);
		
		if(theSupplier == null) {
			
			//throw new RuntimeException("Supplier id not found: " + supplierId );
			return theSupplier;
		} 
		else {
			return theSupplier;
		}
		
	}
	
	//add mapping for POST /suppliers - add new supplier
	
	@PostMapping("/suppliers")
	public EntitySupplier addSupplier(@RequestBody EntitySupplier theSupplier) {		
		theSupplier.setSupplierId(0);	
		EntitySupplier returnSupplier = serviceSupplier.save(theSupplier);	
		return returnSupplier;
	}
	
	//add mapping for PUT /suppliers - update existing supplier
	
	@PutMapping("/suppliers")
	public EntitySupplier updateSupplier(@RequestBody EntitySupplier theSupplier) {
		
		serviceSupplier.save(theSupplier);
		
		return theSupplier;
	}
	
	//add mapping for DELETE /suppliers/{supplierId}	
	@DeleteMapping("/suppliers/{supplierId}")
	public int deleteSupplier(@PathVariable int supplierId) {
		
		EntitySupplier theSupplier = serviceSupplier.findById(supplierId);
		
		if(theSupplier == null) {
			throw new RuntimeException("Supplier id not found: " + supplierId);
		}		
			serviceSupplier.deleteById(supplierId);		
		return supplierId;
		
	}
}







