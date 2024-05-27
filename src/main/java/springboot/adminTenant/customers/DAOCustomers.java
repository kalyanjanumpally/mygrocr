package springboot.adminTenant.customers;

import java.util.List;


public interface DAOCustomers {


	public ResponseCustomers getCustomers(String customerType, Integer itemsPerPage, Integer startIndex);

	public EntityCustomer findById(Integer customerId);

	public Boolean deleteById(Integer customerId);

//	public void resetPassword(DTOPasswordReset dTOPasswordReset);

	public List<EntityCustomer> searchCustomerByPhoneNo(String customerType, String phoneNo);

}
