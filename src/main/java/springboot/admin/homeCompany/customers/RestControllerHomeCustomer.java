package springboot.admin.homeCompany.customers;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
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
public class RestControllerHomeCustomer {
	
	private ServiceHomeCustomer serviceHomeCompanyCustomer;
	
	@Autowired
	public RestControllerHomeCustomer(ServiceHomeCustomer theServiceHomeCompanyCustomer) {
		serviceHomeCompanyCustomer = theServiceHomeCompanyCustomer;		
	}
	
	@GetMapping("/search-customer/{search}/{customerType}/{itemsPerPage}/{startIndex}")
	public ResponseCustomersHome searchCustomer(@PathVariable String search, @PathVariable String customerType, @PathVariable Integer itemsPerPage, 
												@PathVariable Integer startIndex) {
		
		return serviceHomeCompanyCustomer.searchCustomer(search, customerType, itemsPerPage, startIndex);	
	}
	
	@GetMapping("/search-customer-by-phone-no/{customerType}/{phoneNo}")
	public List<EntityCustomerHome> searchCustomerbyPhoneNo(@PathVariable String customerType, @PathVariable String phoneNo ) {
		
		return serviceHomeCompanyCustomer.searchCustomerByPhoneNo(customerType, phoneNo);
	}
	
	@GetMapping("get-customers/{customerType}/{itemsPerPage}/{startIndex}")
	public ResponseCustomersHome getCustomers(@PathVariable String customerType,  @PathVariable Integer itemsPerPage, @PathVariable Integer startIndex) {
		
		return serviceHomeCompanyCustomer.getCustomers(customerType, itemsPerPage, startIndex);			
	}
	
	@PostMapping("/customer")
	public EntityCustomerHome addCustomer(@RequestBody EntityCustomerHome customer) {			
		customer.setCustomerId(0);	
		EntityCustomerHome returnCustomer = serviceHomeCompanyCustomer.save(customer);
		
		return returnCustomer;

	}
	
	@PutMapping("/edit-customer")
	public void editCustomer(@RequestBody EntityCustomerHome customer) throws SQLException {
	
		EntityCustomerHome returnCustomer = serviceHomeCompanyCustomer.editCustomer(customer);

	}

	
	@PutMapping("/reset-customer-password")
	public void resetCustomerPassword(@RequestBody DTOPasswordResetHome dTOPasswordReset){
		
		serviceHomeCompanyCustomer.resetPassword(dTOPasswordReset);
		
	}
	
	
	@PutMapping("/delete-customer")
	public Boolean deleteCustomer(@RequestBody Integer customerId) {
		
		EntityCustomerHome theCustomer = serviceHomeCompanyCustomer.findById(customerId);
		if(theCustomer == null) {
			throw new RuntimeException("Customer id not found: " + customerId);
		}
		return serviceHomeCompanyCustomer.deleteById(customerId);			
	}
	

}
