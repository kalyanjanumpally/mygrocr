package springboot.admin.homeCompany.tenants;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import springboot.admin.homeCompany.customers.EntityCustomerHome;
import springboot.admin.homeCompany.images.EntityImageHome;
import springboot.auth.payloads.MessageResponse;



@CrossOrigin
@RestController
@RequestMapping("/api")
public class RestControllerHomeTenant {
	
	//quick: inject employee DAO (use Constructor injection)

	private ServiceHomeTenant serviceHomeCompany;
	
	@Autowired
	public RestControllerHomeTenant(ServiceHomeTenant theServiceHomeCompany) {
		serviceHomeCompany = theServiceHomeCompany;
	}
	
	@PostMapping(path = "/admin/create-tenant")
	public Boolean addTenant(@RequestBody EntityTenant tenant) throws Exception{		
			
		return serviceHomeCompany.save(tenant);		
	}
	
	
	@PutMapping(path = "/admin/edit-tenant")
	public void editTenant(@RequestBody EntityTenant tenant) {	
		
		System.out.println("tenant date created: " + tenant.getTenantDateTimeCreated());
			
		serviceHomeCompany.editTenant(tenant);		
	}
	
	
	@PutMapping(path = "/admin/edit-tenant-url")
	public Boolean editTenantUrl(@RequestBody EntityTenant tenant) throws SQLException {		
			
		return serviceHomeCompany.editTenantUrl(tenant);		
	}
	
	/*
	@PutMapping(path = "/admin/update-tenant-details-by-tenant")
	public void updateTenantDetailsByTenant(@RequestBody DTOTenantDetails tenantDetails) {	
		
		serviceHomeCompany.updateTenantDetailsByTenant(tenantDetails);
			
		//return serviceHomeCompany.editTenantUrl(tenant);		
	}
	*/
	
	
	@GetMapping("/admin/get-tenants/{itemsPerPage}/{startIndex}")
	public ResponseTenantHome findAllTenants(@PathVariable Integer itemsPerPage, @PathVariable Integer startIndex) {
		
		return serviceHomeCompany.findAllTenants(itemsPerPage, startIndex);
	}
	
	@GetMapping("/admin/search-tenant-by-name/{search}/{itemsPerPage}/{startIndex}")
	public ResponseTenantHome searchTenantByName(@PathVariable String search, @PathVariable Integer itemsPerPage, @PathVariable Integer startIndex) {
		
		return serviceHomeCompany.searchTenantByName(search, itemsPerPage, startIndex);
	}

	
	@GetMapping("/admin/tenant/{tenantId}")
	public EntityTenant findbyId(@PathVariable Integer tenantId) {
		
		return serviceHomeCompany.findById(tenantId);
	}
	
	@GetMapping("/admin/get-tenant-from-url/{tenantUrl}")
	public EntityTenant findbyTenantUrl(@PathVariable String tenantUrl) {
		
		return serviceHomeCompany.findByTenantUrl(tenantUrl);
	}
	
	@GetMapping("/check-tenant-active-status/{tenantUrl}")
	public EntityTenant checkTenantActiveStatus(@PathVariable String tenantUrl) {
		
		EntityTenant dbTenant = serviceHomeCompany.checkTenantActiveStatus(tenantUrl);
		
	//	LocalTime currentTime = LocalTime.now();
	//	System.out.println("currentTime: " + currentTime);
		
		return dbTenant;

	}
	
	
	@GetMapping("/check-tenant-active-status-from-id/{tenantId}")
	public EntityTenant checkTenantActiveStatusFromId(@PathVariable Integer tenantId) {
	
		EntityTenant dbTenant = serviceHomeCompany.checkTenantActiveStatusFromId(tenantId);
		
		LocalTime currentTime = LocalTime.now();
		
		return dbTenant;

	}	
	
	@GetMapping("/admin/search-tenant/{search}/{tenantCity}/{tenantArea}/{itemsPerPage}/{startIndex}")
	public ResponseTenantsHome searchTenant(@PathVariable String search, @PathVariable String tenantCity, @PathVariable String tenantArea, 
			@PathVariable Integer itemsPerPage, @PathVariable Integer startIndex) {
		
		return serviceHomeCompany.searchTenant(search, tenantCity, tenantArea, itemsPerPage, startIndex);

	}
	
	
	@PutMapping("/admin/reset-tenant-password")
	public Boolean resetEmployeePassword(@RequestBody DTOEmployeeTenantPasswordReset dTOPasswordReset) throws SQLException{
		
		return serviceHomeCompany.resetTenantEmployeePassword(dTOPasswordReset);
		
	}
	
	
	@PostMapping("/fetch-stores-in-location")
	public List<EntityTenant> fetchStoresInLocation(){
		
		return serviceHomeCompany.fetchStoresInLocation();
		
	}
	
	
	@PostMapping("/admin/save-store-photo")
	public ShopImageUrl saveShopImage(@RequestPart MultipartFile file) throws Exception {	
				
		return serviceHomeCompany.saveShopImage(file);
		/*
		ShopImageUrl imageUrl = new ShopImageUrl();
		imageUrl.setShopImgUrl("test-url");
		
		return imageUrl;
		*/
	}	
	
	 
	
	@GetMapping("/shop-images/{fileName}")
    public Resource downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = serviceHomeCompany.loadFileAsResource(fileName);
        
        return resource;

    }
    
    
	


}







