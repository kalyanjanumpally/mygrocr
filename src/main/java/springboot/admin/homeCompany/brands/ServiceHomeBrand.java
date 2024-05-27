package springboot.admin.homeCompany.brands;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceHomeBrand {

	
	private DAOHomeBrand dAOHomeBrand;
	
	@Autowired
	public ServiceHomeBrand(DAOHomeBrand theDAOHomeBrand) {
		
		dAOHomeBrand = theDAOHomeBrand;
	}
	
	@Transactional
	public List<EntityBrandHome> findAll() {
			
		return dAOHomeBrand.findAll();
	}

	@Transactional
	public void addNewBrand(EntityBrandHome brand) throws Exception {
		
		dAOHomeBrand.addNewBrand(brand);
	}
	
	@Transactional
	public void editBrand(EntityBrandHome brand) throws Exception {
				
		dAOHomeBrand.editBrand(brand);
	}

	@Transactional
	public EntityBrandHome findById(Integer brandId) {
		
		return dAOHomeBrand.findById(brandId);
	}

	@Transactional
	public Boolean deleteById(Integer brandId) throws Exception {
		return dAOHomeBrand.deleteById(brandId);
		
	}


}
