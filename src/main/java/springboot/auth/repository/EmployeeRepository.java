package springboot.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import springboot.auth.entities.EntityEmployee;

@Repository
public interface EmployeeRepository extends JpaRepository<EntityEmployee, Integer> {
	Optional<EntityEmployee> findByUsername(String username);

	Boolean existsByUsername(String username);
	
	Boolean existsByUsernameAndTenant(String username, String tenant);
	
	Optional<EntityEmployee> findByUsernameAndTenant(String username, String tenant);
	
	

}
	

