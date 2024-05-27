package springboot.adminTenant.batches;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.hibernate.mapping.Array;

public class csvHelperBatches {

  public static ByteArrayInputStream orderStockToCSV(List<DTONonPerishablesOrder> orderStock) {
    
	final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
    
    try (    	
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);
    ) 
    {	
    	csvPrinter.printRecord(Arrays.asList("Product","Sale-Last To Last 30 Days", "Sale- Last 30 Days", "Actual Stock", 
    										"Order"));
    	
    	
    	for (DTONonPerishablesOrder order : orderStock) {
            List<String> data = Arrays.asList(
             /*     String.valueOf(order.getOrderId()),
                  String.valueOf(order.getDateDelivered()),
                  order.getCustomer().getFullName(),
                  order.getOrderSourceType(),
                  String.valueOf(order.getSubTotal()),
                  order.getOrderDeliveryStatus(),
                  order.getPayments().get(order.getPayments().size()-1).getPaymentMode() */
            		);
            csvPrinter.printRecord(data);
          }
          csvPrinter.flush();
          return new ByteArrayInputStream(out.toByteArray());
    } 
    catch (IOException e) {
          throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
    }
  }

public static ByteArrayInputStream batchesToCSV(List<?> batches) {
	final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
    
    try (    	
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);
    ) 
    {	
    	Map<String, Object[]> itemsList = new HashMap<String, Object[]>();
    	
    	 Iterator iter=batches.iterator();
         while(iter.hasNext())      	 
         {       	 
        	 /* itemsList- (itemsList is a map having key of productName)
        	  * 0 - brand
        	  * 1 - quantity
        	  * 2 - Total
        	  * 3 - gst
        	  * 4 - hsn 
        	  */
        	 
        	 /* row[0] - order Id
        	  * row[1] - order Delivery Status
        	  * row[2] - batch Id
        	  * row[3] - product Id
        	  * row[4] - quantity
        	  * row[5] - mrp
        	  * row[6] - gst
        	  * row[7] - hsn code
        	  * row[8] - product name
        	  * row[9] - brand
        	  */
        	 
             Object row[] = (Object[])iter.next(); 
             
             if(itemsList.containsKey(row[8])){ 
            	 
            	 if(row[1].equals("Delivered") || row[1].equals("Completed")) {
 					
						Object tempArray[] = { row[9],
											  (Integer) itemsList.get(row[8])[1] + (Integer)row[4],
										      (Float)itemsList.get(row[8])[2] + ((Integer)row[4]).floatValue() * (Float)row[5],
										       row[6], 
										       row[7]};
						itemsList.put((String)row[8] , tempArray);
					}
					else if(row[1].equals("Sales Return")) {
						Object tempArray[] = {row[9],
											  (Integer) itemsList.get(row[8])[1] - (Integer) row[4],
							               	  (Float) itemsList.get(row[8])[2] - ((Integer)row[4]).floatValue() * (Float)row[5],
					              			   row[6], 
					              			   row[7]};
						itemsList.put((String)row[8] , tempArray);					
					} 	 
             }
             else {
            	 if(row[1].equals("Delivered")  || row[1].equals("Completed")) {
						Object tempArray[] = {row[9] , (Integer)row[4] , ((Integer)row[4]).floatValue() * (Float)row[5], row[6], row[7]};
						itemsList.put((String)row[8] , tempArray);
					}
					else if(row[1].equals("Sales Return")) {
						Object tempArray[] = {row[9] , (Integer) row[4] * (-1) , ((Integer)row[4]).floatValue() * (Float)row[5] * (-1), row[6], row[7] };
						itemsList.put((String)row[8] , tempArray);					
					} 
             }
         } 
         
         csvPrinter.printRecord(Arrays.asList("Product", "Brand", "Quantity", "Average MRP", "Total", "GST", "HSN Code"));
         
         for (String productName : itemsList.keySet()) {
        	 List<String> data = Arrays.asList(	 
        			 productName,
        			 String.valueOf(itemsList.get(productName)[0]),
        			 String.valueOf(itemsList.get(productName)[1]),
        			 String.valueOf(Math.round((Float)itemsList.get(productName)[2]/ (Integer)itemsList.get(productName)[1]) ),
        			 String.valueOf(itemsList.get(productName)[2]),
        			 String.valueOf(itemsList.get(productName)[3]),
        			 String.valueOf(itemsList.get(productName)[4])
        			 ); 
        	 csvPrinter.printRecord(data); 
         }
         
          csvPrinter.flush();
          return new ByteArrayInputStream(out.toByteArray());
    } 
    catch (IOException e) {
          throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
    }
}

}
