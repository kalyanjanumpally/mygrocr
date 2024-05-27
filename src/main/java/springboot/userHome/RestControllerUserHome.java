package springboot.userHome;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin
@RestController
//@RequestMapping("/api/user")
public class RestControllerUserHome {
	
	@Autowired
	private ServiceUserHome serviceUser;
/*
	@GetMapping("/")
	public String testApi() {
		return "Connection Successful.";
	}
*/	
	@PostMapping(path = "/api/user/create-online-order")
	public void addOnlineOrderInvoice(@RequestBody DtoOnlineOrderDataUserHome dtoOrders) throws Exception {				
		
		serviceUser.saveOnlineOrder(dtoOrders);	
	}
	
	@PutMapping(path = "/api/user/order-cancel/{tenantId}/{orderId}")
	public void cancelOrder(@PathVariable Integer tenantId, @PathVariable Long orderId) throws Exception {		
			
		serviceUser.cancelOrder(tenantId, orderId);
	}
	
	
	@GetMapping("/api/user/get-orders-of-customer/{customerId}/{itemsPerPage}/{startIndex}")
	public ResponseOrdersUserHome getOrdersOfCustomer(@PathVariable Integer customerId,
												 @PathVariable Integer itemsPerPage, @PathVariable Integer startIndex) throws Exception {
		
		return serviceUser.getOrdersOfCustomer(customerId, itemsPerPage, startIndex);
	}
	
	@PutMapping("/api/user/change-customer-delivery-address")
	public Boolean changeDeliveryAddress(@RequestBody EntityCustomerUserHome customer) throws SQLException {
		
		Boolean addressChangeStatus = serviceUser.changeDeliveryAddress(customer);	
		return addressChangeStatus;
		//return serviceCustomers.getCustomers(itemsPerPage, startIndex);			
	}
	
	@PutMapping("/api/user/update-customer-account-details")
	public Boolean updateAccountDetails(@RequestBody EntityCustomerUserHome customer) throws SQLException {
		
		Boolean updateAccountStatus = serviceUser.updateAccount(customer);	
		return updateAccountStatus;
		//return serviceCustomers.getCustomers(itemsPerPage, startIndex);			
	}
	
	

}
