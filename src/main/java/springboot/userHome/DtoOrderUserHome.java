package springboot.userHome;

import java.sql.Date;
import java.util.ArrayList;

import antlr.collections.List;
import lombok.Data;

@Data
public class DtoOrderUserHome {
	
	private Long orderId;
	private Long orderHomeId;
	private Long salesReturnId;
	private Integer tenantId;
	private String tenantName;
	private String tenantUrl;
	private Integer numberOfBatches;
	private float subTotal;	
	private float pendingPayment;
	private String paymentMode;
	private float shippingCharges;		
	private String orderDeliveryStatus;		
	private String orderSourceType;
	private java.util.Date dateTimeCreated;	
	private java.util.Date dateTimeDelivered;	
	private Integer shippingAddressId;	
	private String comments;
	private ArrayList<DTOCustomerPaymentsUserHome> payments;
	
}
