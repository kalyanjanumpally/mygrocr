package springboot.adminTenant.batches;

import lombok.Data;

@Data
public class DTONonPerishablesOrder {
	
	private String productName;
	
	private Float saleLast30Days;
	
	private Float saleLastToLast30Days;
	
	private Float actualStock;
	
	private Float orderStock;

}
