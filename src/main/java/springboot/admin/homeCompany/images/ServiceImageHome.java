package springboot.admin.homeCompany.images;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import springboot.admin.images.Exceptions.FileStorageException;
import springboot.admin.images.Exceptions.MyFileNotFoundException;

import springboot.properties.FileStorageProperties;


@Service
public class ServiceImageHome {
	
	
    private final Path fileStorageLocation;
    private DAOImageHome dAOImage;
    private EntityManager entityManager;
    
    /*
    @Value("${file.upload-dir}")
    private String baseDirectory;
    */
    
 
    
    public ServiceImageHome() {
		this.fileStorageLocation = null;  	
    }

    @Autowired
    public ServiceImageHome(FileStorageProperties fileStorageProperties, DAOImageHome theDAOImage, EntityManager theEntityManager) {
        
    	this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    	dAOImage = theDAOImage;
    	entityManager = theEntityManager;
    }
	
	@Transactional
	public List<EntityImageHome> saveImage(MultipartFile[] file) {
		
		List<EntityImageHome> imageList = new ArrayList<EntityImageHome>();
		
		for (MultipartFile fileIter : file) {
			String fileName = StringUtils.cleanPath(fileIter.getOriginalFilename());
	        try {
	            // Check if the file's name contains invalid characters
	            if(fileName.contains("..")) {
	                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
	            }
	
	            // Copy file to the target location (Replacing existing file with the same name)
	            Path targetLocation = this.fileStorageLocation.resolve(fileName);
	            Files.copy(fileIter.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
	            /*           
	            String dir = Paths.get(baseDirectory, tenantUrl).toString();
	            
	            File directory = new File(dir);
	            if (!directory.exists()) {
	                directory.mkdir();
	            }
	            
	            String filePath = Paths.get(baseDirectory, tenantUrl, fileName).toString();
	            Files.copy(fileIter.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
	            */
	
	        //    return fileName;
	        } catch (IOException ex) {
	            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
	        } 
	        
	  /*       String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
	        .path("/api/images/") 
	        .path(fileName)
	        .toUriString(); */
	         
	         String fileDownloadUri = "http://localhost:5000/api/images/" + fileName ; 
	         
	    //     imageList.add(repositoryImage.save(new EntityImageHome(fileName, fileDownloadUri)));
	         
	         imageList.add(dAOImage.save(new EntityImageHome(fileName, fileDownloadUri)));
		}
		return imageList;
		
	}
	
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }

	public ResponseImagesHome findAll(Integer itemsPerPage, Integer startIndex) {
		return dAOImage.findAll(itemsPerPage, startIndex);
	}
	
    @Transactional
    public ResponseImagesHome searchImages (String searchTerm, Integer itemsPerPage, Integer startIndex) { 

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        
        try {
			fullTextEntityManager.createIndexer().startAndWait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(EntityImageHome.class).get();
        Query luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1).onFields("imageName")
                .matching(searchTerm).createQuery();

        javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, EntityImageHome.class);

        // execute search
        
		jpaQuery.setFirstResult(startIndex);
		jpaQuery.setMaxResults(itemsPerPage);

        List<EntityImageHome> dbImages = null;
        try {
            dbImages = jpaQuery.getResultList();
        } catch (NoResultException nre) {
            ;// do nothing
        }
        
        for(EntityImageHome image : dbImages) {
        	System.out.println(image.getImageName());
        }
        
        ResponseImagesHome responseImages = new ResponseImagesHome();
        
		if(startIndex == 0) {
			Integer countResults = ((FullTextQuery) jpaQuery).getResultSize();
			responseImages.setCountOfImages( Long.valueOf(countResults));
			System.out.println("count: " + countResults);
		}
		

		
    	responseImages.setImages(dbImages);
        
        return responseImages;
    }

}
