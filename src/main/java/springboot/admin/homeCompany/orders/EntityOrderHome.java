package springboot.admin.homeCompany.orders;

import java.sql.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import springboot.admin.homeCompany.customers.EntityCustomerHome;



@Getter
@Setter
@Entity
@Table(name="orders")
public class EntityOrderHome {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="order_id")
	private Long orderId;
	
	@Column(name="tenant_id")
	private Integer tenantId;
	
	@Column(name="order_no")
	private Long orderNo;
	
	@Column(name="order_return_no")
	private Long orderReturnNo;	
	
	@Column(name="order_delivery_status")
	private String orderDeliveryStatus;
		
	//@Temporal(TemporalType.TIMESTAMP)
	@Column(name="date_time_delivered")
	private java.util.Date dateTimeDelivered;
	
	@Column(name="date_time_created")
	private java.util.Date dateTimeCreated;	


	@ManyToOne(fetch=FetchType.EAGER, cascade= 
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name="customer_id")
	private EntityCustomerHome customer;
	
	
	//define constructors
	public EntityOrderHome() {		
	}


	@Override
	public String toString() {
		return "EntityOrderHome [orderId=" + orderId + ", tenantId=" + tenantId + ", orderNo=" + orderNo
				+ ", orderReturnNo=" + orderReturnNo + ", orderDeliveryStatus=" + orderDeliveryStatus
				+ ", dateTimeDelivered=" + dateTimeDelivered
				+ ", dateTimeCreated=" + dateTimeCreated + ", customer=" + customer + "]";
	}


	public EntityOrderHome(Integer tenantId, Long orderNo, Long orderReturnNo, String orderDeliveryStatus,
			 java.util.Date dateTimeDelivered, java.util.Date dateTimeCreated,
			EntityCustomerHome customer) {
		super();
		this.tenantId = tenantId;
		this.orderNo = orderNo;
		this.orderReturnNo = orderReturnNo;
		this.orderDeliveryStatus = orderDeliveryStatus;
		this.dateTimeDelivered = dateTimeDelivered;
		this.dateTimeCreated = dateTimeCreated;
		this.customer = customer;
	}

}
