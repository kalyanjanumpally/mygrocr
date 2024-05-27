package springboot.admin.homeCompany.customers;

import java.util.List;

import lombok.Data;

@Data
public class ResponseCustomersHome {	
	
	List<EntityCustomerHome> customers;
	
	Long countOfCustomers;

}
