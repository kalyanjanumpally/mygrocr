package springboot.admin.homeCompany.orders;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;




@CrossOrigin
@RestController
@RequestMapping("/api/admin")
public class RestControllerHomeOrders {
	
	//quick: inject employee DAO (use Constructor injection)
	@Autowired
	private ServiceHomeOrders serviceHomeOrders;
	
	@GetMapping("/get-orders/{itemsPerPage}/{startIndex}")
	public ResponseEntity<?> findAllOrders(@PathVariable Integer itemsPerPage, @PathVariable Integer startIndex) throws SQLException {

		ResponseOrdersHome responseOrders = serviceHomeOrders.findAllOrders(itemsPerPage, startIndex);
		
		return ResponseEntity.ok(responseOrders);
	}
	
	@PutMapping(path = "/order-cancel")
	public Boolean cancelOrder(@RequestBody Long orderId) throws SQLException {			
		
		return serviceHomeOrders.cancelOrder(orderId);
	}
	
	@GetMapping("/pending-orders/{itemsPerPage}/{startIndex}")
	public ResponseEntity<?> findPendingOrders(@PathVariable Integer itemsPerPage, @PathVariable Integer startIndex) throws SQLException {
	
		ResponseOrdersHome responseOrders = serviceHomeOrders.findPendingOrders(itemsPerPage, startIndex);
		
		return ResponseEntity.ok(responseOrders);
	}
	
	
	@GetMapping("/get-orders-of-customer/{customerId}/{itemsPerPage}/{startIndex}")
	public ResponseOrdersHome getOrdersOfCustomer(@PathVariable Integer customerId,
			 @PathVariable Integer itemsPerPage, @PathVariable Integer startIndex ) throws SQLException{
		return serviceHomeOrders.getOrdersOfCustomer(customerId, itemsPerPage, startIndex);
	}
	
	@GetMapping("/find-order-by-orderid/{orderId}")
	public DtoOrderAndBatchesHome findOrderByOrderId(@PathVariable Long orderId) throws SQLException {
		return serviceHomeOrders.findOrderByOrderId(orderId);

	}
	
	@GetMapping("/get-orders-of-tenant/{tenantId}/{itemsPerPage}/{startIndex}")
	public ResponseOrdersHome getOrdersOfTenant(@PathVariable Integer tenantId,
			 @PathVariable Integer itemsPerPage, @PathVariable Integer startIndex ) throws SQLException{
		return serviceHomeOrders.getOrdersOfTenant(tenantId, itemsPerPage, startIndex);
	}
	/*
	@PostMapping(path = "/create-online-order")
	public void addOnlineOrderInvoice(@RequestBody DtoOrdersUser dtoOrders) {	
		
	}
	*/
	
	@GetMapping("/orders-batches-in-dates-range/{customerType}/{fromDateString}/{toDateString}")
	public List<DtoOrderAndBatchesHome> findOrdersAndBatchesInDatesRange(@PathVariable String customerType, @PathVariable String fromDateString, @PathVariable String toDateString) throws SQLException {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		Date fromDate = new Date();
		Date toDate = new Date();
		
		try {
			fromDate = dateFormat.parse(fromDateString + " 00:00:00");
			toDate = dateFormat.parse(toDateString + " 23:59:59");
			
			System.out.println("restcontroller, fromDate: " + fromDate);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}  
		
		return serviceHomeOrders.findOrdersAndBatchesInDatesRange(fromDate, toDate, customerType);
	}
	
	
	
	
	/*
	@GetMapping("/pending-orders/{orderSourceType}")
	public ResponseOrdersHome findAllPendingOnlineOrders(@PathVariable String orderSourceType) {

		ResponseOrdersHome responseOrders = new ResponseOrdersHome();
		responseOrders.setDtos(serviceHomeOrders.findAllPendingOrders(orderSourceType));
		return responseOrders;
	}
	

	
	@GetMapping("/order/{orderId}")
	public DtoOrders findOrderbyId(@PathVariable Integer orderId) {
		
		return serviceHomeOrders.findOrderById(orderId);

	}
	
	

	
	
	@GetMapping("/orders-in-dates-range-download/{customerType}/{fromDateString}/{toDateString}")
	public ResponseEntity<Resource> findOrdersInDatesRangeDownload(@PathVariable String customerType, @PathVariable String fromDateString, @PathVariable String toDateString) {
		
	//	String fromDateString = dates.getFromDate();
	//	String toDateString = dates.getToDate();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		Date fromDate = new Date();
		Date toDate = new Date();
		
		try {
			fromDate = dateFormat.parse(fromDateString);
			toDate = dateFormat.parse(toDateString);
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		
		String filename = "SalesReport.csv";
	    InputStreamResource file = new InputStreamResource(serviceHomeOrders.findOrdersInDatesRangeDownload(fromDate, toDate, customerType));
	    return ResponseEntity.ok()
	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
	        .contentType(MediaType.parseMediaType("application/csv"))
	        .body(file);	
	}
	
	@GetMapping("/products-in-dates-range-download/{customerType}/{fromDateString}/{toDateString}")
	public ResponseEntity<Resource> findProductsSalesInDatesRangeDownload(@PathVariable String customerType, @PathVariable String fromDateString, @PathVariable String toDateString) {
		
	//	String fromDateString = dates.getFromDate();
	//	String toDateString = dates.getToDate();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		Date fromDate = new Date();
		Date toDate = new Date();
		
		try {
			fromDate = dateFormat.parse(fromDateString);
			toDate = dateFormat.parse(toDateString);
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		
		String filename = "ItemsSalesReport.csv";
	    InputStreamResource file = new InputStreamResource(serviceHomeOrders.findProductSalesInDatesRangeDownload(fromDate, toDate, customerType));
	    return ResponseEntity.ok()
	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
	        .contentType(MediaType.parseMediaType("application/csv"))
	        .body(file);	
	}
	*/
	

	
	
	
}







