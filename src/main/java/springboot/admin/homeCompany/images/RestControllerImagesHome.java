package springboot.admin.homeCompany.images;

import java.io.File;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.core.io.Resource;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;


@CrossOrigin 
@RestController
@RequestMapping("/api")
public class RestControllerImagesHome {
	
	private ServiceImageHome serviceImage;

	
	@Autowired
	public RestControllerImagesHome( ServiceImageHome theServiceImage) {
		serviceImage = theServiceImage;
	}
	

	@PostMapping(value = "/images")
	public List<EntityImageHome> saveImage(@RequestPart MultipartFile[] file) throws Exception {	
				
		return serviceImage.saveImage(file);
	} 
	
	@GetMapping("/images/{itemsPerPage}/{startIndex}") 
	public ResponseImagesHome findAllImages(@PathVariable Integer itemsPerPage, @PathVariable Integer startIndex) {
		
		return serviceImage.findAll(itemsPerPage, startIndex);		
	}
	
	@GetMapping("/images/{fileName}")
    public Resource downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
		
        Resource resource = serviceImage.loadFileAsResource(fileName);
        
        return resource;

    }  
	
	@GetMapping("/search-images/{searchTerm}/{itemsPerPage}/{startIndex}") 
	public ResponseImagesHome searchImages(@PathVariable String searchTerm, @PathVariable Integer itemsPerPage, 
			@PathVariable Integer startIndex) {
		
		return serviceImage.searchImages(searchTerm, itemsPerPage, startIndex);
		
	}
	
/*
	

	
	

	*/		
}







