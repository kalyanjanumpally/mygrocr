package springboot.adminTenant.suppliers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ServiceSupplier {

	private DAOSuppliers dAOSuppliers;
	
	@Autowired
	public ServiceSupplier(DAOSuppliers theSuppliersDAO) {		
		dAOSuppliers = theSuppliersDAO;
	}
	
	
	@Transactional
	public List<EntitySupplier> findAll() {			
		return dAOSuppliers.findALL();
	}

	
	@Transactional
	public EntitySupplier findById(int theId) {		
		return dAOSuppliers.findById(theId);
	}

	
	@Transactional
	public EntitySupplier save(EntitySupplier theSupplier) {
		return dAOSuppliers.save(theSupplier);
	}

	
	@Transactional
	public void deleteById(int theId) {
		dAOSuppliers.deleteById(theId);
	}

}
