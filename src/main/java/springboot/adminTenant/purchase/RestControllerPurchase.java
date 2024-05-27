package springboot.adminTenant.purchase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import springboot.adminTenant.orders.DtoOrders;
import springboot.adminTenant.orders.ResponseOrders;



@CrossOrigin
@RestController
@RequestMapping("/api/admin-tenant")
public class RestControllerPurchase {
	
	//quick: inject employee DAO (use Constructor injection)

	private ServicePurchase servicePurchase;
	
	@Autowired
	public RestControllerPurchase(ServicePurchase theServicePurchase) {
		servicePurchase = theServicePurchase;
	}
	
	//add mapping for POST /suppliers - add new supplier
	
	@PostMapping(path = "/purchase-invoice")
	public void addPurchaseInvoice(@RequestBody DtoPurchase dtoPurchase) {			
			
		servicePurchase.save(dtoPurchase);	
		
	}
	
	@PutMapping(path = "/purchase-update")
	public void updatePurchase(@RequestBody DtoPurchase dto) {			

		servicePurchase.updatePurchase(dto);
	}
	
	@GetMapping(path = "/purchase-invoice/{purchaseInvoiceId}")
	public DtoPurchase getPurchase(@PathVariable Long purchaseInvoiceId) {	
			
		return servicePurchase.findById(purchaseInvoiceId);			
	}	
	
	@GetMapping("/purchase-invoice-and-return/{purchaseInvoiceId}")
	public List<DtoPurchase> getPurchaseInvoiceAndReturn(@PathVariable Long purchaseInvoiceId) {
		return servicePurchase.getPurchaseInvoiceAndReturn(purchaseInvoiceId);

	}
	
			
	@PostMapping("/create-purchase-return")
	public void CreatePurchseReturn(@RequestBody DtoPurchase dto) {
		servicePurchase.savePurchaseReturn(dto);
	}
	
	@GetMapping("/get-purchases/{itemsPerPage}/{startIndex}")
	public ResponsePurchases findAllPurchases(@PathVariable Integer itemsPerPage, @PathVariable Integer startIndex) {

		return servicePurchase.findAllPurchases(itemsPerPage, startIndex);
	}
	
	@GetMapping("/find-initial-stock-purchase-invoice/{fromDateString}/{toDateString}")
	public DtoPurchase findInitialStockPurchaseInvoice(@PathVariable String fromDateString, @PathVariable String toDateString) throws ParseException {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		Date fromDate = new Date();
		Date toDate = new Date();
		
		try {
			fromDate = dateFormat.parse(fromDateString);
			toDate = dateFormat.parse(toDateString);
		} catch (ParseException e) {
			//e.printStackTrace();
			throw e;
		} 
		
		return servicePurchase.findInitialStockPurchaseInvoice(fromDate, toDate); 
	}
	
}







