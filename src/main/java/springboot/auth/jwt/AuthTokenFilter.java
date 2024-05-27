package springboot.auth.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import springboot.TenantContext;
import springboot.auth.services.UserDetailsServiceImpl;
import springboot.auth.services.UserDetailsServiceImplCustomer;


public class AuthTokenFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private UserDetailsServiceImplCustomer userDetailsServiceCustomer;

	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		try {
			
			String jwt = parseJwt(request);	
			
			String tenant = request.getHeader("tenant-url");			
			TenantContext.setCurrentTenant(tenant);
			
			if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
				
				String username = jwtUtils.getUserNameFromJwtToken(jwt);
			
				// checking for email format
				
				String regexPattern = "^(.+)@(\\S+)$";
				Boolean customerVerifyBool = Pattern.compile(regexPattern)
			      .matcher(username)
			      .matches();
				
				if(customerVerifyBool.equals(false)) {
					
					String tenantIdentifier = "verificationDataSource";
					
					UserDetails userDetails = userDetailsService.loadUserByUsername(username);
					
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
				else {
					
					UserDetails userDetails = userDetailsServiceCustomer.loadUserByUsername(username);
				
					List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
					authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));	
				
					UsernamePasswordAuthenticationToken authenticationCustomer = new UsernamePasswordAuthenticationToken(
						userDetails, null, authorities);
					authenticationCustomer.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					SecurityContextHolder.getContext().setAuthentication(authenticationCustomer);
					
				}
				
				
			}
		} catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e); 
		//	logger.error("Cannot set user authentication: {}");

		}


		filterChain.doFilter(request, response);
	}

	private String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");

		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7, headerAuth.length());
		}

		return null;
	}

}
