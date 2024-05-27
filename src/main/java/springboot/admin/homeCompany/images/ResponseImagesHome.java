package springboot.admin.homeCompany.images;

import java.util.List;

import lombok.Data;

@Data
public class ResponseImagesHome {
	
	List<EntityImageHome> images;
	
	Long countOfImages;

}
