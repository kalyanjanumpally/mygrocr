package springboot.adminTenant.batches;

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
public class EntityOrderBatches {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="order_id")
	private Long orderId;
	
	@Column(name="sales_return_id")
	private Long salesReturnId;	
	
	@Column(name="number_of_batches")
	private Integer numberOfBatches;
	
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
	
	@Column(name="date_delivered")
	private Date dateDelivered;
	
	@Column(name="shipping_address_id")
	private Integer shippingAddressId;
	
	@Column(name="comments")
	private String comments;
	
	/*
	@ManyToOne(fetch=FetchType.EAGER, cascade= 
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name="customer_id")
	private EntityCustomersOrders customer;
	
	@OneToMany(mappedBy="order", cascade= 
	{CascadeType.DETACH, CascadeType.MERGE, 
		CascadeType.PERSIST, CascadeType.REFRESH})
	private List<EntityCustomerPaymentsOrders> payments; */
	
	//define constructors
	public EntityOrderBatches() {		
	}

	@Override
	public String toString() {
		return "EntityOrderBatches [orderId=" + orderId + ", salesReturnId=" + salesReturnId + ", numberOfBatches="
				+ numberOfBatches + ", subTotal=" + subTotal + ", pendingPayment=" + pendingPayment
				+ ", shippingCharges=" + shippingCharges + ", orderDeliveryStatus=" + orderDeliveryStatus
				+ ", orderSourceType=" + orderSourceType + ", dateTimeCreated=" + dateTimeCreated + ", dateDelivered="
				+ dateDelivered + ", shippingAddressId=" + shippingAddressId + ", comments=" + comments + "]";
	}

	public EntityOrderBatches(Long salesReturnId, Integer numberOfBatches, float subTotal, float pendingPayment,
			float shippingCharges, String orderDeliveryStatus, String orderSourceType, java.util.Date dateTimeCreated,
			Date dateDelivered, Integer shippingAddressId, String comments) {
		super();
		this.salesReturnId = salesReturnId;
		this.numberOfBatches = numberOfBatches;
		this.subTotal = subTotal;
		this.pendingPayment = pendingPayment;
		this.shippingCharges = shippingCharges;
		this.orderDeliveryStatus = orderDeliveryStatus;
		this.orderSourceType = orderSourceType;
		this.dateTimeCreated = dateTimeCreated;
		this.dateDelivered = dateDelivered;
		this.shippingAddressId = shippingAddressId;
		this.comments = comments;
	}



}
