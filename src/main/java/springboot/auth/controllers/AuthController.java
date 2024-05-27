package springboot.auth.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;


import springboot.auth.entities.ERole;
import springboot.auth.entities.EntityCustomerAuth;
import springboot.auth.entities.EntityEmployee;
import springboot.auth.entities.Role;
import springboot.auth.jwt.JwtUtils;
import springboot.auth.payloads.JwtResponse;
import springboot.auth.payloads.JwtResponseCustomer;
import springboot.auth.payloads.LoginRequest;
import springboot.auth.payloads.LoginRequestCustomer;
import springboot.auth.payloads.MessageResponse;
import springboot.auth.payloads.SignupRequest;
import springboot.auth.repository.CustomerRepository;
import springboot.auth.repository.EmployeeRepository;
import springboot.auth.repository.RoleRepository;
import springboot.auth.services.UserDetailsImpl;
import springboot.auth.services.UserDetailsImplCustomer;
import springboot.auth.payloads.CustomerRegistrationResponse;
import springboot.auth.payloads.DTOPasswordTenantEmployees;
import springboot.auth.payloads.DTOPasswords;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	EmployeeRepository employeeRepository;
	
	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	JwtUtils jwtUtils;
	


	@PostMapping("/admin-signin")
	public ResponseEntity<?> authenticateAdminUser(@Valid @RequestBody LoginRequest loginRequest) {
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();	
		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getEmployeeId(), 
												 userDetails.getUsername(), 
											//	 userDetails.getEmail(),
												 roles));
	}

	@PostMapping("/admin-signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (employeeRepository.existsByUsernameAndTenant(signUpRequest.getUsername(), signUpRequest.getTenant())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}

		// Create new user's account
		EntityEmployee employee = new EntityEmployee(signUpRequest.getUsername(), signUpRequest.getTenant(), 
						//	 signUpRequest.getEmail(),
							 encoder.encode(signUpRequest.getPassword()), true);

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByRoleName(ERole.ROLE_MANAGER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByRoleName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				case "manager":
					Role modRole = roleRepository.findByRoleName(ERole.ROLE_MANAGER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(modRole);

					break; 
				default:
					Role userRole = roleRepository.findByRoleName(ERole.ROLE_MANAGER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}

		employee.setRoles(roles);
		employeeRepository.save(employee);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
	
	@GetMapping("/validate-employee-session")
	public Boolean validateEmployeeSession(@RequestHeader("authorization") String headerJwt) {
		
		String jwtToken = headerJwt.substring(7);

		return jwtUtils.validateJwtToken(jwtToken);
		
	}
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestCustomer loginRequestCustomer) {
		
		System.out.println("Reaching signin");
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequestCustomer.getEmail(), loginRequestCustomer.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String jwt = jwtUtils.generateJwtTokenCustomer(authentication);
		
		UserDetailsImplCustomer userDetailsCustomer = (UserDetailsImplCustomer) authentication.getPrincipal();	
		
		/*
		List<String> roles = userDetailsCustomer.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList()); */

		return ResponseEntity.ok(new JwtResponseCustomer(jwt, 
												 userDetailsCustomer.getCustomerId(), 
												 userDetailsCustomer.getFirstName(),
												 userDetailsCustomer.getLastName(),
												 userDetailsCustomer.getEmail(),
												 userDetailsCustomer.getAddress(),
												 userDetailsCustomer.getCity(),
												 userDetailsCustomer.getPhoneNo1(),
												 userDetailsCustomer.getPhoneNo2(),
												 userDetailsCustomer.getPostalCode(),
												 userDetailsCustomer.getState() 
												 ));
	}
	
	
	@PostMapping("/signup")
	public CustomerRegistrationResponse registerCustomer(@Valid @RequestBody EntityCustomerAuth newCustomer) {
		
		CustomerRegistrationResponse response = new CustomerRegistrationResponse();
		
		if (customerRepository.existsByEmail(newCustomer.getEmail())  || customerRepository.existsByPhoneNo1(newCustomer.getPhoneNo1()) ) {
			response.setRegistrationStatus(false);
			return response;
		}
		
		newCustomer.setPassword(encoder.encode(newCustomer.getPassword()));
	//	newCustomer.setFullName(newCustomer.getFirstName() + " " + newCustomer.getLastName());
		
		customerRepository.save(newCustomer);
		response.setRegistrationStatus(true);

		return response;
	}
	
	@GetMapping("/validate-customer-session")
	public ResponseEntity validateCustomerSession(@RequestHeader("authorization") String headerJwt) {
		
		String jwtToken = headerJwt.substring(7);

		Boolean validationBool = jwtUtils.validateJwtToken(jwtToken);
				
		if(validationBool == false) {
			return null;
		}
		else {
				
			String email = jwtUtils.getUserNameFromJwtToken(jwtToken);
			
			// Use the customerRepository to retrieve customer details by email.
	        Optional<EntityCustomerAuth> customerDetails = customerRepository.findByEmail(email);

	        if (customerDetails.isPresent()) {
	            EntityCustomerAuth customer = customerDetails.get();
	            return ResponseEntity.ok(customer); // Return customer details with HTTP status 200 (OK).
	        } else {
	            return ResponseEntity.notFound().build(); // Return HTTP status 404 (Not Found) if no customer found.
	        }
		}	
	}
	
	@PutMapping("/change-password")
	public Boolean changeCustomerPassword(@RequestBody DTOPasswords passwords) {
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(passwords.getEmail(), passwords.getOldPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		UserDetailsImplCustomer userDetailsCustomer = (UserDetailsImplCustomer) authentication.getPrincipal();
		
		Optional<EntityCustomerAuth> dbCustomerOptional = customerRepository.findById(userDetailsCustomer.getCustomerId());
		
		if(dbCustomerOptional.isPresent()) {
			
			EntityCustomerAuth dbCustomer = dbCustomerOptional.get();		
			dbCustomer.setPassword(encoder.encode(passwords.getNewPassword()));		
			customerRepository.save(dbCustomer);
		}
		
		return true;
	}
	
	
	@PutMapping("/change-tenant-employee-password")
	public Boolean changeTenantEmployeePassword(@RequestBody DTOPasswordTenantEmployees passwords) {

		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(passwords.getUsername(), passwords.getOldPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		
		Optional<EntityEmployee> dbEmployeeOptional = employeeRepository.findById(userDetails.getEmployeeId());
		
		if(dbEmployeeOptional.isPresent()) {
			
			EntityEmployee dbEmployee = dbEmployeeOptional.get();		
			dbEmployee.setPassword(encoder.encode(passwords.getNewPassword()));		
			employeeRepository.save(dbEmployee);
		}
		
		return true;
	}
		
	
	
	
	
}