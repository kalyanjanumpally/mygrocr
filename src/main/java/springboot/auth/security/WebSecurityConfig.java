package springboot.auth.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

//import springboot.auth.controllers.DaoAuthenticationProvider;
//import springboot.auth.controllers.ProviderManager;
import springboot.auth.jwt.AuthEntryPointJwt;
import springboot.auth.jwt.AuthTokenFilter;
//import springboot.auth.jwt.AuthTokenFilterCustomer;
import springboot.auth.jwt.JwtUtils;
import springboot.auth.services.UserDetailsServiceImpl;
import springboot.auth.services.UserDetailsServiceImplCustomer;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
		// securedEnabled = true,
		// jsr250Enabled = true,
		prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	UserDetailsServiceImpl userDetailsServiceEmployee;
	
	@Autowired
	UserDetailsServiceImplCustomer userDetailsServiceCustomer;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;
	
	

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	} 
	
/*	
	@Override
	public void configure (AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {

		authenticationManagerBuilder.userDetailsService(userDetailsServiceCustomer).passwordEncoder(passwordEncoder());
    	authenticationManagerBuilder.userDetailsService(userDetailsServiceEmployee).passwordEncoder(passwordEncoder());
	}
*/
	
	@Bean
	public AuthenticationProvider employeeAuthenticationProvider() {
	    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
	    provider.setUserDetailsService(userDetailsServiceEmployee);
	    provider.setPasswordEncoder(passwordEncoder());
	    return provider;
	}

	@Bean
	public AuthenticationProvider customerAuthenticationProvider() {
	    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
	    provider.setUserDetailsService(userDetailsServiceCustomer);
	    provider.setPasswordEncoder(passwordEncoder());
	    return provider;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	    auth.authenticationProvider(employeeAuthenticationProvider())
	        .authenticationProvider(customerAuthenticationProvider());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable()
			.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.authorizeRequests()                                 //.authorizeRequests()
			.antMatchers("/api/admin/**").hasRole("MANAGER")
			.antMatchers("/api/admin-tenant/**").hasRole("MANAGER")
			.antMatchers("/api/user/**").hasRole("CUSTOMER")
	//		.requestMatchers()
	//		.antMatchers("/api/user/**").and().authorizeRequests().anyRequest().hasRole("ROLE_CUSTOMER")
		//	.anyRequest().authenticated();
			.anyRequest().permitAll();
		//	.and()
	    //    .apply(new TenantAuthenticationConfigurer<>(verificationDataSource))
	    //        .tenantDetailsSource(new TenantHeaderDetailsSource()) // Custom tenant identifier extraction
	    //        .tenantAuthenticationProvider(new CustomAuthenticationProvider()); // Your custom authentication provider
		
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	}
}