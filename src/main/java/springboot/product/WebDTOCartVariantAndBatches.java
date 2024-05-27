package springboot.product;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class WebDTOCartVariantAndBatches {
	
//	private WebDTOVariant variant;
	
	private WebEntityProductVariantByUnit variant;
	
//	private List<WebDTOBatchData> batchesData;
	
	private List<WebEntityBatchesProduct> batchesData;
	

}
