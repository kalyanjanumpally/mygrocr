package springboot.product;

import java.util.List;

import lombok.Data;

@Data
public class WebResponseProducts {
	
	private List<WebDTOProductAndBatches> dtos;
	
	private Long countOfProducts;

}
