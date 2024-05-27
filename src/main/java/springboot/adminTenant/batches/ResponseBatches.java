package springboot.adminTenant.batches;

import java.util.List;

import lombok.Data;

@Data
public class ResponseBatches {
	
	List<DTOVariantBatches> dtos;
	
	private Long countOfBatches;

}
