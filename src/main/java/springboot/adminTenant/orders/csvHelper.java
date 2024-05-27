package springboot.adminTenant.orders;

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

public class csvHelper {

  public static ByteArrayInputStream ordersToCSV(List<EntityOrder> orders) {
    
	final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
    
    try (    	
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);
    ) 
    {	
    	csvPrinter.printRecord(Arrays.asList("Order Id","Delivery/Return Date", "Customer", "Source", 
    										"Total", "Delivery Status", "Payment Status"));
    	
    	
    	for (EntityOrder order : orders) {
            List<String> data = Arrays.asList(
                  String.valueOf(order.getOrderId()),
                  String.valueOf(order.getDateTimeDelivered()),
                  order.getCustomer().getFullName(),
                  order.getOrderSourceType(),
                  String.valueOf(order.getSubTotal()),
                  order.getOrderDeliveryStatus(),
                  order.getPayments().get(order.getPayments().size()-1).getPaymentMode());
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
        	 /* itemsList- (itemsList is a map having key of productName + unit)
        	  * 0 - product name
        	  * 1 - unit
        	  * 2 - brand name
        	  * 3 - quantity
        	  * 4 - total
        	  * 5 - gst
        	  * 6 - hsn code
        	  */
        	 
        	 /* row[0] - order Id
        	  * row[1] - order Delivery Status
        	  * row[2] - batch Id
        	  * row[3] - batch product Id
        	  * row[4] - batch variant Id
        	  * row[5] - batch product name
        	  * row[6] - batch unit
        	  * row[7] - batch brand name
        	  * row[8] - quantity
        	  * row[9] - mrp
        	  * row[10] - gst
        	  * row[11] - hsn code
        	  */
        	 
             Object row[] = (Object[])iter.next(); 
             
             if(itemsList.containsKey(row[5]+" "+row[6])){ 
            	 
            	 if(row[1].equals("Delivered") || row[1].equals("Completed")) {
            		 
            		 	if(row[10] == null) {
            		 		row[10] = "";
            		 	}
            		 	if(row[11] == null) {
            		 		row[11] = "";
            		 	}
 					
						Object tempArray[] = { row[5], row[6], row[7],
								              (Float)itemsList.get(row[5]+" "+row[6])[3] + (Float) row[8],
										      (Float)itemsList.get(row[5]+" "+row[6])[4] + ((Float)row[8]) * (Float)row[9],
										       row[10], 
										       row[11]};
						itemsList.put((String)(row[5]+" "+row[6]) , tempArray);
					}
					else if(row[1].equals("Sales Return")) {
						
            		 	if(row[10] == null) {
            		 		row[10] = "";
            		 	}
            		 	if(row[11] == null) {
            		 		row[11] = "";
            		 	}
						
						Object tempArray[] = {row[5], row[6], row[7],
											  (Float) itemsList.get(row[5]+" "+row[6])[3] - (Float) row[8],
							               	  (Float) itemsList.get(row[5]+" "+row[6])[4] - ((Float)row[8]) * (Float)row[9],
					              			   row[10], 
					              			   row[11]};
						itemsList.put((String)(row[5]+" "+row[6]) , tempArray);					
					} 	 
             }
             else {
            	 if(row[1].equals("Delivered")  || row[1].equals("Completed")) {
            		 
         		 	if(row[10] == null) {
        		 		row[10] = "";
         		 	}
        		 	if(row[11] == null) {
        		 		row[11] = "";
        		 	}
            		 
					Object tempArray[] = {row[5], row[6], row[7], (Float)row[8] , ((Float)row[8]) * (Float)row[9], row[10], row[11]};
					itemsList.put((String)(row[5]+" "+row[6]) , tempArray);
				 }
				 else if(row[1].equals("Sales Return")) {
					 
	         		 if(row[10] == null) {
	        		 	row[10] = "";
	        		 }
	        		 if(row[11] == null) {
	        		 	row[11] = "";
	        		 }
					 
					 Object tempArray[] = {row[5], row[6], row[7], (Float) row[8] * (-1) , ((Float)row[8]) * (Float)row[9] * (-1), row[10], row[11] };
					 itemsList.put((String)(row[5]+" "+row[6]) , tempArray);					
				 } 
             }
         } 
         
         csvPrinter.printRecord(Arrays.asList("Product", "Unit", "Brand", "Quantity", "Average MRP", "Total", "GST", "HSN Code"));
         
         for (String productName : itemsList.keySet()) {
        	 List<String> data = Arrays.asList(	 
        			 String.valueOf(itemsList.get(productName)[0]),
        			 String.valueOf(itemsList.get(productName)[1]),
        			 String.valueOf(itemsList.get(productName)[2]),
        			 String.valueOf(itemsList.get(productName)[3]),
        			 String.valueOf(Math.round((Float)itemsList.get(productName)[4]/ (Float)itemsList.get(productName)[3]) ),
        			 String.valueOf(itemsList.get(productName)[4]),
        			 String.valueOf(itemsList.get(productName)[5]),
        			 String.valueOf(itemsList.get(productName)[6])
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
