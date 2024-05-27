package springboot.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import springboot.auth.entities.EntityCustomerAuth;
import springboot.auth.entities.EntityEmployee;

@Repository
public interface CustomerRepository extends JpaRepository<EntityCustomerAuth, Integer> {
	Optional<EntityCustomerAuth> findByEmail(String email);

	Boolean existsByEmail(String email);

	Boolean existsByPhoneNo1(String phoneNo1);
	
	Optional<EntityCustomerAuth> findByCustomerId(Integer customerId);
	
}
	

