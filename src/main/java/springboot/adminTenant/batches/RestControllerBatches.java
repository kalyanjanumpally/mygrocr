package springboot.adminTenant.batches;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;



@CrossOrigin
@RestController
@RequestMapping("/api/admin-tenant")
public class RestControllerBatches {
	
	//quick: inject employee DAO (use Constructor injection)

	private ServiceBatches serviceBatches;
	
	@Autowired
	public RestControllerBatches(ServiceBatches theServiceBatches) {
		serviceBatches = theServiceBatches;
	}
	
	@GetMapping("/variant-inventory-data/{variantId}/{itemsPerPage}/{startIndex}")
	public ResponseBatches getBatches(@PathVariable Integer variantId, @PathVariable Integer itemsPerPage,
									@PathVariable Integer startIndex) {
					
		return serviceBatches.findBatches(variantId, itemsPerPage, startIndex);	
	}  
	
	/*
	@GetMapping("/product-batches-quantity-data/{productId}")
	public List<DtoBatchAndProductBatches> getPurchaseBatches(@PathVariable int productId) {
					
		return serviceBatches.findPurchaseBatches(productId);	
	}
	*/
	
	@GetMapping("/get-purchase-batches/{variantId}/{startIndex}/{itemsPerPage}")
	public ResponseBatches getPurchaseBatches(@PathVariable Integer variantId, @PathVariable Integer startIndex, @PathVariable Integer itemsPerPage ) {
		
		return serviceBatches.getPurchaseBatches(variantId, startIndex, itemsPerPage);
	}
	/*
	@PutMapping("/update-selling-price-of-batch/{batchNo}/{variantId}/{sellingPrice}")
	public void updateSellingPriceOfBatch(@PathVariable String batchNo, @PathVariable Integer variantId, @PathVariable Float sellingPrice){
		
		serviceBatches.updateSellingPriceOfBatch(batchNo, variantId, sellingPrice);
	}
	*/
	
	@PutMapping("/batch-display-change/{batchNo}/{variantId}")
	public void batchDisplayChange(@PathVariable String batchNo, @PathVariable Integer variantId) {
					
		serviceBatches.batchDisplayChange(batchNo, variantId);	
	}
	
	
	@GetMapping("/expiry-stock-in-dates-range/{fromDateString}/{toDateString}")
	public List<EntityBatch> findExpiryStockInDatesRange(@PathVariable String fromDateString, @PathVariable String toDateString) {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		Date fromDate = new Date();
		Date toDate = new Date();
		
		try {
			fromDate = dateFormat.parse(fromDateString);
			toDate = dateFormat.parse(toDateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}  
		
		return serviceBatches.findExpiryStockInDatesRange(fromDate, toDate);
	}
	
	@GetMapping("/download-non-perishables-order/{brandId}")
	public ResponseEntity<Resource> downloadNonPerishablesOrder (@PathVariable Integer brandId) {
		
	/*		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			Date fromDate = new Date();
			Date toDate = new Date();
			
			try {
				fromDate = dateFormat.parse(fromDateString);
				toDate = dateFormat.parse(toDateString);
			} catch (ParseException e) {
				e.printStackTrace();
			} */
			
			String filename = "NonPerishablesOrder.csv";
		    InputStreamResource file = new InputStreamResource(serviceBatches.downloadNonPerishablesOrder(brandId));
		    return ResponseEntity.ok()
		        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
		        .contentType(MediaType.parseMediaType("application/csv"))
		        .body(file);
		
	}
	
	@PutMapping("/batches-update-url")
	public void updateBatches (@RequestBody DTOBatchesUpdate dtoBatches) {
		
		serviceBatches.updateBatches(dtoBatches);
		
	}

	/*
	@PostMapping("/update-stock")
	public void updateStock(@RequestBody List<DTOStockUpdate> stockUpdateData) {	
		
		serviceBatches.updateStock(stockUpdateData);	
	}
	*/
	
}







