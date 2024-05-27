package springboot.product;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class WebDTOProductAndBatches {
	
	private WebEntityProduct product;
	
	//private List<WebDTOBatchData> batchesData;
	
	private List<WebEntityBatchesProduct> batchesData;
	

}
