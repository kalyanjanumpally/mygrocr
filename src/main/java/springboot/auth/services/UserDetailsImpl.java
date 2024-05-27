package springboot.auth.services;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework. security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import springboot.auth.entities.EntityEmployee;
import springboot.auth.entities.Role;

public class UserDetailsImpl implements UserDetails {
	
	private static final long serialVersionUID = 1L;

	private Integer employeeId;

	private String username;
	
	private String tenant;
	
	private Boolean employeeActive;

	@JsonIgnore
	private String password;

	private Collection<? extends GrantedAuthority> authorities;

	public UserDetailsImpl(Integer employeeId, String username, String tenant, Boolean employeeActive, String password,
			Collection<? extends GrantedAuthority> authorities) {
		this.employeeId = employeeId;
		this.username = username;
		this.tenant = tenant;
		this.employeeActive = employeeActive;
		this.password = password;
		this.authorities = authorities;
	}

	public static UserDetailsImpl build(EntityEmployee employee) {
		
		Set<Role> roles = employee.getRoles();
		
		List<GrantedAuthority> authorities = employee.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
				.collect(Collectors.toList());

		return new UserDetailsImpl(
				employee.getEmployeeId(), 
				employee.getUsername(),
				employee.getTenant(),
				employee.getEmployeeActive(),
				employee.getPassword(), 
				authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		
		if(employeeActive == false) {
			return false;
		}
		else {
			return true;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl employee = (UserDetailsImpl) o;
		return Objects.equals(employeeId, employee.employeeId);
	}
}
