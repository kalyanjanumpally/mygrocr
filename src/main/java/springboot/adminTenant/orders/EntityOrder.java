package springboot.adminTenant.orders;

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
public class EntityOrder {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="order_id")
	private Long orderId;
	
	@Column(name="number_of_batches")
	private Integer numberOfBatches;
	
	@Column(name="sales_return_id")
	private Long salesReturnId;	
	
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
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="date_time_created")
	private java.util.Date dateTimeCreated;
	
	@Column(name="date_created")
	private java.util.Date dateCreated;
	
	@Column(name="date_time_delivered")
	private java.util.Date dateTimeDelivered;
	
	@Column(name="shipping_address_id")
	private Integer shippingAddressId;
	
	@Column(name="comments")
	private String comments;
	
	
	@ManyToOne(fetch=FetchType.EAGER, cascade= 
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name="customer_id")
	private EntityCustomersOrders customer;
	
	@OneToMany(mappedBy="order", cascade= 
	{CascadeType.DETACH, CascadeType.MERGE, 
		CascadeType.PERSIST, CascadeType.REFRESH})
	private List<EntityCustomerPaymentsOrders> payments;
	
	//define constructors
	public EntityOrder() {		
	}

	@Override
	public String toString() {
		return "EntityOrder [orderId=" + orderId + ", numberOfBatches=" + numberOfBatches + ", salesReturnId="
				+ salesReturnId + ", subTotal=" + subTotal + ", pendingPayment=" + pendingPayment + ", shippingCharges="
				+ shippingCharges + ", orderDeliveryStatus=" + orderDeliveryStatus 
				+ ", orderSourceType=" + orderSourceType + ", dateTimeCreated=" + dateTimeCreated
				+ ", dateCreated=" + dateCreated + ", dateTimeDelivered=" + dateTimeDelivered + ", shippingAddressId="
				+ shippingAddressId + ", comments=" + comments + ", customer=" + customer + ", payments=" + payments
				+ "]";
	}

	public EntityOrder(Integer numberOfBatches, Long salesReturnId, float subTotal, float pendingPayment,
			float shippingCharges, String orderDeliveryStatus, String orderSourceType,
			java.util.Date dateTimeCreated, java.util.Date dateCreated, java.util.Date dateTimeDelivered,
			Integer shippingAddressId, String comments, EntityCustomersOrders customer,
			List<EntityCustomerPaymentsOrders> payments) {
		super();
		this.numberOfBatches = numberOfBatches;
		this.salesReturnId = salesReturnId;
		this.subTotal = subTotal;
		this.pendingPayment = pendingPayment;
		this.shippingCharges = shippingCharges;
		this.orderDeliveryStatus = orderDeliveryStatus;
		this.orderSourceType = orderSourceType;
		this.dateTimeCreated = dateTimeCreated;
		this.dateCreated = dateCreated;
		this.dateTimeDelivered = dateTimeDelivered;
		this.shippingAddressId = shippingAddressId;
		this.comments = comments;
		this.customer = customer;
		this.payments = payments;
	}

}
