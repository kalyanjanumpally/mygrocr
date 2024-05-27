package springboot.adminTenant.customers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import springboot.adminTenant.product.EntityProduct;





@CrossOrigin
@RestController
@RequestMapping("/api/admin-tenant")
public class RestControllerCustomers {
	
	//quick: inject employee DAO (use Constructor injection)

	private ServiceCustomerSearch serviceCustomers;
	
	@Autowired
	public RestControllerCustomers(ServiceCustomerSearch theServiceCustomers) {
		serviceCustomers = theServiceCustomers;
	}

	
	@GetMapping("/search-customer/{search}/{customerType}/{itemsPerPage}/{startIndex}")
	public ResponseCustomers searchCustomer(@PathVariable String search, @PathVariable String customerType, @PathVariable Integer itemsPerPage, 
												@PathVariable Integer startIndex) {
		
		return serviceCustomers.searchCustomer(search, customerType, itemsPerPage, startIndex);	
	}
	
	@GetMapping("/search-customer-by-phone-no/{customerType}/{phoneNo}")
	public List<EntityCustomer> searchCustomerbyPhoneNo(@PathVariable String customerType, @PathVariable String phoneNo ) {
		
		return serviceCustomers.searchCustomerByPhoneNo(customerType, phoneNo);
	}
	
	@GetMapping("get-customers/{customerType}/{itemsPerPage}/{startIndex}")
	public ResponseCustomers getCustomers(@PathVariable String customerType,  @PathVariable Integer itemsPerPage, @PathVariable Integer startIndex) {
		
		return serviceCustomers.getCustomers(customerType, itemsPerPage, startIndex);			
	}
	
	@PostMapping("/customer")
	public EntityCustomer addCustomer(@RequestBody EntityCustomer customer) {			
		customer.setCustomerId(0);	
		EntityCustomer returnCustomer = serviceCustomers.save(customer);
		
		return returnCustomer;

	}
	
	@PutMapping("/edit-customer")
	public void editCustomer(@RequestBody EntityCustomer customer) {			
	
		EntityCustomer returnCustomer = serviceCustomers.editCustomer(customer);

	}
	
	@PutMapping("/delete-customer")
	public Boolean deleteCustomer(@RequestBody Integer customerId) {
		
		EntityCustomer theCustomer = serviceCustomers.findById(customerId);
		if(theCustomer == null) {
			throw new RuntimeException("Customer id not found: " + customerId);
		}
		return serviceCustomers.deleteById(customerId);			
	}
	
	/*
	@PutMapping("/reset-customer-password")
	public void resetCustomerPassword(@RequestBody DTOPasswordReset dTOPasswordReset){
		
		serviceCustomers.resetPassword(dTOPasswordReset);
		
	}
	
	*/
	
	
}







