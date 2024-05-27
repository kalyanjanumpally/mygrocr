package springboot.adminTenant.customers;

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


@Getter
@Setter
@Entity
@Table(name="orders")
public class EntityOrderCustomer {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="order_id")
	private Long orderId;
	
/*	@Column(name="sales_return_id")
	private Integer salesReturnId;	
	
	@Column(name="sub_total")
	private float subTotal;
	
	@Column(name="pending_payment")
	private float pendingPayment;
	
	@Column(name="shipping_charges")
	private float shippingCharges;
		
	@Column(name="order_delivery_status")
	private String orderDeliveryStatus;	
	
	@Column(name="order_source_type")
	private String orderSourceType;
	
	@Column(name="date_created")
	private Date dateCreated;
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="date_time_created")
	private java.util.Date dateTimeCreated;
	
	@Column(name="date_delivered")
	private Date dateDelivered;
	
	@Column(name="shipping_address_id")
	private Integer shippingAddressId;
	
	@Column(name="comments")
	private String comments; */
	
	
	@ManyToOne(fetch=FetchType.EAGER, cascade= 
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name="customer_id")
	private EntityCustomer customer;
	
	//define constructors
	public EntityOrderCustomer() {		
	}

	public EntityOrderCustomer(EntityCustomer customer) {
		this.customer = customer;
	}

	@Override
	public String toString() {
		return "EntityOrderCustomer [orderId=" + orderId + ", customer=" + customer + "]";
	}

}
