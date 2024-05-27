package springboot.userHome;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceUserHome {
	
	@Autowired
	private DAOUserHome dAOUserHome;
	
	@Transactional
	public ResponseOrdersUserHome getOrdersOfCustomer(Integer customerId, Integer itemsPerPage,
			Integer startIndex) throws Exception {

		ResponseOrdersUserHome responseOrders;
		
		responseOrders = dAOUserHome.findOrdersOfCustomer(customerId, itemsPerPage, startIndex);
		
		return responseOrders;
	}
	
	@Transactional
	public void saveOnlineOrder(DtoOnlineOrderDataUserHome dtoOrder) throws Exception {
		
		DtoOrderUserHome order = dtoOrder.getOrder();
		order.setOrderId((long) 0);
		
	//	DtoCustomerHome dtoCustomer = dtoOrder.getDtoCustomer();

		dAOUserHome.saveOnlineOrderInTenant(dtoOrder);
		
	}

	@Transactional
	public void cancelOrder(Integer tenantId, Long orderId) throws Exception {
		
		dAOUserHome.cancelOrder(tenantId, orderId);		
	}
	
	@Transactional
	public Boolean changeDeliveryAddress(EntityCustomerUserHome customer) throws SQLException {

		return dAOUserHome.changeDeliveryAddress(customer);

	}
	
	@Transactional
	public Boolean updateAccount(EntityCustomerUserHome customer) throws SQLException {

		return dAOUserHome.updateAccount(customer);
	}
	
	

}
