package springboot.admin.homeCompany.customers;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface RepositoryCustomersHome extends CrudRepository<EntityCustomerHome,Integer> {

	Boolean existsByEmail(String email);

	Boolean existsByPhoneNo1(String phoneNo1);

}
