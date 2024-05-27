package springboot.adminTenant.customers;

import java.util.List;

import lombok.Data;

@Data
public class ResponseCustomers {	
	
	List<EntityCustomer> customers;
	
	Long countOfCustomers;

}
