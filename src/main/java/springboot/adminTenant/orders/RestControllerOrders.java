package springboot.adminTenant.orders;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

import springboot.auth.payloads.MessageResponse;



@CrossOrigin
@RestController
//@RequestMapping("/api/admin-tenant")
public class RestControllerOrders {
	
	//quick: inject employee DAO (use Constructor injection)

	private ServiceOrders serviceOrders;
	
	@Autowired
	public RestControllerOrders(ServiceOrders theServiceOrders) {
		serviceOrders = theServiceOrders;
	}
	
	@PostMapping(path = "/api/admin-tenant/create-order")
	public void addOrderInvoice(@RequestBody DtoOrders dtoOrders) {		
			
		serviceOrders.save(dtoOrders);		
	}
	/*
	@PostMapping(path = "/create-b2b-order")
	public void addB2BOrderInvoice(@RequestBody DtoOrders dtoOrders) {		
			
		serviceOrders.saveB2BOrder(dtoOrders);		
	}
	*/
	
    @GetMapping(value = "/api/new-order-audio-file", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getAudioFile() {
    	
    	return serviceOrders.getNewOrderAudioFile();
   
    }
	
	
	@PostMapping(path = "/api/admin-tenant/create-sales-return")
	public void salesReturn(@RequestHeader("tenant-url") String tenantUrl,  @RequestBody DtoOrders dto) throws SQLException {			
			
		serviceOrders.saveSalesReturn(dto, tenantUrl);
	}
	
	@PostMapping(path = "/api/admin-tenant/save-customer-payments")
	public void saveCustomerPayments(@RequestBody List<DtoPayment> dtoPayments) {			
			
		serviceOrders.saveCustomerPayments(dtoPayments);
	}
	
	@GetMapping("/api/admin-tenant/get-orders/{orderSourceType}/{itemsPerPage}/{startIndex}")
	public ResponseEntity<?> findAllOrders(@PathVariable String orderSourceType, @PathVariable Integer itemsPerPage, @PathVariable Integer startIndex) {
		
		ResponseOrders responseOrders = serviceOrders.findAllOrders(orderSourceType, itemsPerPage, startIndex);
		
		return ResponseEntity.ok(responseOrders);
	}
	/*
	@GetMapping("/get-orders-salesonly-online-phone/{itemsPerPage}/{startIndex}")
	public ResponseEntity<?> findAllOrdersSalesOnlyOnlinePhone(@PathVariable Integer itemsPerPage, @PathVariable Integer startIndex) {

		ResponseOrders responseOrders = serviceOrders.findAllOrdersSalesOnlyOnlinePhone(itemsPerPage, startIndex);
		
		return ResponseEntity.ok(responseOrders);
	}
	*/
	
	@GetMapping("/api/admin-tenant/pending-orders/{orderSourceType}")
	public ResponseOrders findAllPendingOnlineOrders(@PathVariable String orderSourceType) {

		ResponseOrders responseOrders = new ResponseOrders();
		responseOrders.setDtos(serviceOrders.findAllPendingOrders(orderSourceType));
		return responseOrders;
	}
	
	@GetMapping("/api/admin-tenant/orders")
	public List<DtoOrders> findAllOrders() {

		return serviceOrders.findAllOrders();
	}
	
	@GetMapping("/api/admin-tenant/order/{orderId}")
	public DtoOrders findbyId(@PathVariable Long orderId) {
		
		return serviceOrders.findById(orderId);

	}
	
	@GetMapping("/api/admin-tenant/find-order-by-orderid/{orderSourceType}/{orderId}")
	public DtoOrders findOrderByOrderId(@PathVariable String orderSourceType, @PathVariable Long orderId) {
		return serviceOrders.findOrderByOrderId(orderSourceType, orderId);

	}
	
	@GetMapping("/api/admin-tenant/order-sales-return/{orderId}")
	public List<DtoOrders> getOrderSalesReturn(@PathVariable Long orderId) {
		return serviceOrders.getOrderSalesReturn(orderId);

	}
	
	@GetMapping("/api/admin-tenant/orders-with-pending-payments/{customerType}")
	public List<EntityOrder> getOrdersWithPendingPayments(@PathVariable String customerType) {
		return serviceOrders.getOrdersWithPendingPayments(customerType);

	}	
	
	@GetMapping("/api/admin-tenant/get-orders-of-customer/{orderSourceType}/{customerId}/{itemsPerPage}/{startIndex}")
	public ResponseOrders getOrdersOfCustomer(@PathVariable String orderSourceType, @PathVariable Integer customerId,
												 @PathVariable Integer itemsPerPage, @PathVariable Integer startIndex) {
		
		return serviceOrders.getOrdersOfCustomer(orderSourceType, customerId, itemsPerPage, startIndex);

	}
	
		
	
	@GetMapping("/api/admin-tenant/orders-batches-in-dates-range/{customerType}/{fromDateString}/{toDateString}")
	public List<DtoOrders> findOrdersInDatesRange(@PathVariable String customerType, @PathVariable String fromDateString, @PathVariable String toDateString) {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		Date fromDate = new Date();
		Date toDate = new Date();
		
		try {
			fromDate = dateFormat.parse(fromDateString);
			toDate = dateFormat.parse(toDateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}  
		
		return serviceOrders.findBatchesOrdersInDatesRange(fromDate, toDate, customerType);
	}
	
	
	@GetMapping("/api/admin-tenant/orders-in-dates-range-download/{customerType}/{fromDateString}/{toDateString}")
	public ResponseEntity<Resource> findOrdersInDatesRangeDownload(@PathVariable String customerType, @PathVariable String fromDateString, @PathVariable String toDateString) throws UnsupportedEncodingException {
		
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
		String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());

	    InputStreamResource file = new InputStreamResource(serviceOrders.findOrdersInDatesRangeDownload(fromDate, toDate, customerType));
	    
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename);
	    
        /*
	    return ResponseEntity.ok()
	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
	        .contentType(MediaType.parseMediaType("application/csv"))
	    	.
	        .body(file);
	        */	
	    
        return ResponseEntity.ok()
                .headers(headers)
                .body(file);
	}
	
	@GetMapping("/api/admin-tenant/products-in-dates-range-download/{customerType}/{fromDateString}/{toDateString}")
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
	    InputStreamResource file = new InputStreamResource(serviceOrders.findProductSalesInDatesRangeDownload(fromDate, toDate, customerType));
	    return ResponseEntity.ok()
	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
	        .contentType(MediaType.parseMediaType("text/csv"))
	        .body(file);	
	}
		
	@PutMapping(path = "/api/admin-tenant/order-cancel")
	public void cancelOrder(@RequestHeader("tenant-url") String tenantUrl, @RequestBody Long orderId) throws SQLException {	
			
		serviceOrders.cancelOrder(orderId, tenantUrl);
	}	
	
	@PutMapping(path = "/api/admin-tenant/order-deliver")
	public void deliverOrder(@RequestHeader("tenant-url") String tenantUrl, @RequestBody Long orderId) {			
			
		serviceOrders.deliverOrder(orderId, tenantUrl);
	}
	
	@PutMapping(path = "/api/admin-tenant/order-accept/{orderId}")
	public void acceptOrder(@RequestHeader("tenant-url") String tenantUrl, @PathVariable Long orderId) {			
			
		serviceOrders.acceptOrder(orderId, tenantUrl);
	}
	
	@PutMapping(path = "/api/admin-tenant/order-complete")
	public void completeOrder(@RequestBody Long orderId) {			
			
		serviceOrders.completeOrder(orderId);
	}
	
	@PutMapping(path = "/api/admin-tenant/order-return")
	public void returnOrder(@RequestHeader("tenant-url") String tenantUrl, @RequestBody Long orderId) throws SQLException {			
			
		serviceOrders.returnOrder(orderId, tenantUrl);
	}
	
	@PutMapping(path = "/api/admin-tenant/order-update")
	public void updateOrder(@RequestBody DtoOrders dto) {			
			
		serviceOrders.updateOrder(dto);
	}
	

	

}







