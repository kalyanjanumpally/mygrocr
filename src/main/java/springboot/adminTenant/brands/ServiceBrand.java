package springboot.adminTenant.brands;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceBrand {

	
	private DAOEntityBrand dAOEntityBrand;
	
	@Autowired
	public ServiceBrand(DAOEntityBrand theDAOEntityBrand) {
		
		dAOEntityBrand = theDAOEntityBrand;
	}
	
	
	@Transactional
	public List<EntityBrand> findAll() {
			
		return dAOEntityBrand.findALL();
	}
	
	/*
	@Transactional
	public EntityBrand findById(Integer brandId) {
		
		return dAOEntityBrand.findById(brandId);
	}
	*/
	
	/*
	
	
	@Transactional
	public void addNewBrand(EntityBrand brand) {
		
		dAOEntityBrand.addNewBrand(brand);
	}
	
	
	@Transactional
	public void editBrand(EntityBrand brand) {
				
		dAOEntityBrand.editBrand(brand);
	}

	
	@Transactional
	public Boolean deleteById(Integer brandId) {
		return dAOEntityBrand.deleteById(brandId);
		
	}
	*/


}
