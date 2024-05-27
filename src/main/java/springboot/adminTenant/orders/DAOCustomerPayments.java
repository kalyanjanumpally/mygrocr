package springboot.adminTenant.orders;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DAOCustomerPayments {
	
	private EntityManager entityManager;
	
	//set up constructor injection	
	public DAOCustomerPayments() {	
	}
	
	@Autowired
	public DAOCustomerPayments(EntityManager theEntityManager) {	
		this.entityManager = theEntityManager;
	}

	void save(EntityCustomerPaymentsOrders payment) {
		EntityCustomerPaymentsOrders dbPayment = entityManager.merge(payment);
		
	}

}
