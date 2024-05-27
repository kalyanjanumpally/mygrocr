package springboot.auth.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import springboot.auth.entities.EntityCustomerAuth;
import springboot.auth.entities.EntityEmployee;
import springboot.auth.repository.CustomerRepository;
import springboot.auth.repository.EmployeeRepository;

@Service
public class UserDetailsServiceImplCustomer implements UserDetailsService {
	
	@Autowired
	CustomerRepository customerRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		EntityCustomerAuth customer = customerRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Customer Email Not Found: " + email));
	
		return UserDetailsImplCustomer.build(customer);
	}

}
