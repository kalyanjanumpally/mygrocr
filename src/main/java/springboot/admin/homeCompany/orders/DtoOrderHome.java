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


@Getter
@Setter
public class DtoOrderHome {
	
	//define fields
	
	private Long orderId;
	
	private Long orderHomeId;
	
	private Integer numberOfBatches;
	
	private Long salesReturnId;	
	
	private Integer tenantId;
	
	private String tenantName;
	
	private float subTotal;
	
	private float pendingPayment;
	
	private String paymentMode;
	
	private float shippingCharges;
		
	private String orderDeliveryStatus;	
	
	private String orderSourceType;	
	
	private java.util.Date dateTimeCreated;
	
	private java.util.Date dateCreated;
	
	private Date dateTimeDelivered;
	
//	private Integer shippingAddressId;
	
	private String comments;
	
	private EntityCustomerHomeOrders customer;
	

}
