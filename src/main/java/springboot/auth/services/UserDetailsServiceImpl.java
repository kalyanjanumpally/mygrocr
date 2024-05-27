package springboot.auth.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import springboot.auth.entities.EntityEmployee;
import springboot.auth.repository.EmployeeRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	EmployeeRepository employeeRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		EntityEmployee employee = employeeRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Employee Not Found with username: " + username));

		return UserDetailsImpl.build(employee);
	}
	
}
