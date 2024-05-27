package springboot.adminTenant.customers;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface RepositoryCustomers extends CrudRepository<EntityCustomer,Integer> {

	Boolean existsByEmail(String email);

	Boolean existsByPhoneNo1(String phoneNo1);

}
