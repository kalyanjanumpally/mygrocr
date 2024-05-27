package springboot;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
//import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

//import lombok.var;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TenantInterceptor implements HandlerInterceptor {
 //   private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) {
        /*
    	String path = request.getRequestURI();
        log.info("Path: {}", path);

        var pattern = "/api/{tenantId}/**";
        String tenantId = null;
        if (antPathMatcher.match(pattern, path)) {
            var pathVariables = antPathMatcher.extractUriTemplateVariables(pattern, path);
            tenantId = pathVariables.get("tenantId");
            log.info("Tenant id {}", tenantId);
        }
        TenantContext.setCurrentTenant(tenantId);
		*/
    	
        HttpServletRequest req = (HttpServletRequest) request;
        String tenantName = req.getHeader("tenant-url");
        
        TenantContext.setCurrentTenant(tenantName);
        
        return true;
    }
  

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        TenantContext.clear();
    }


}