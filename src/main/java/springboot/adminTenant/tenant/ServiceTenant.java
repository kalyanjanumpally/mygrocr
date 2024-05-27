package springboot.adminTenant.tenant;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceTenant {

	
	private DAOTenant dAOTenant;
	
	@Autowired
	public ServiceTenant(DAOTenant theDAOTenant) {
		
		dAOTenant = theDAOTenant;
	}
	
	@Transactional
	public void updateTenantDetailsByTenant(DTOTenantDetails tenantDetails) throws Exception {
		
		dAOTenant.updateTenantDetailsByTenant(tenantDetails);
	}

	@Transactional
	public DTOTenantDetails getTenantDetailsFromUrl(String tenantUrl) throws Exception {
		
		return dAOTenant.getTenantDetailsFromUrl(tenantUrl);
	}
	
	


}
