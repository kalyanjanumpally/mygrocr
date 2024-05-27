package springboot.adminTenant.orders;

import javax.persistence.Column;

import lombok.Data;

@Data
public class DtoPayment {
	
	private Long orderId;	
	private Float amount;	
	private String paymentMode;
	
}
