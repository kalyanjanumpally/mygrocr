package springboot.product;

import java.util.List;
import lombok.Data;

@Data
public class WebResponseCartVariants {
		
	private List<WebDTOCartVariantAndBatches> dtos;

}
