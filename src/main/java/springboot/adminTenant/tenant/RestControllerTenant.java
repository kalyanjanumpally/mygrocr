package springboot.adminTenant.tenant;

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
public class RestControllerTenant {
	
	//quick: inject product DAO (use Constructor injection)

	private ServiceTenant serviceTenant;
	
	@Autowired
	public RestControllerTenant(ServiceTenant theServiceTenant) {
		serviceTenant = theServiceTenant;
	}
	
	@GetMapping(path = "/get-tenant-from-url/{tenantUrl}")
	public DTOTenantDetails getTenantDetailsFromUrl(@PathVariable String tenantUrl) throws Exception {
		
		return serviceTenant.getTenantDetailsFromUrl(tenantUrl);
		
	}
	
	@PutMapping(path = "/update-tenant-details")
	public void updateTenantDetailsByTenant(@RequestBody DTOTenantDetails tenantDetails) throws Exception {	
		
		serviceTenant.updateTenantDetailsByTenant(tenantDetails);	
	}
	


}







