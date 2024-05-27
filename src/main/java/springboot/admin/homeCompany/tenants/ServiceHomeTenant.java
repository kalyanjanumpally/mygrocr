package springboot.admin.homeCompany.tenants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
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


import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import springboot.admin.images.Exceptions.FileStorageException;
import springboot.admin.images.Exceptions.MyFileNotFoundException;
import springboot.properties.FileStorageProperties;

@Service
public class ServiceHomeTenant {

	private DAOHomeTenant dAOHomeTenant;
    private EntityManager entityManager;
    

    
    @Value("${file.upload-shop-photos-dir}")
    private String baseDirectory;
    
    private final Path fileStorageLocation;
	
    
	@Autowired
	public ServiceHomeTenant(DAOHomeTenant thedAOHomeTenant, EntityManager theEntityManager, FileStorageProperties fileStorageProperties) {		
		dAOHomeTenant = thedAOHomeTenant;
		entityManager = theEntityManager;
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadShopPhotosDir())
                .toAbsolutePath().normalize();
		
		

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
		

	}
	

	@Transactional
	public Boolean save(EntityTenant tenant) throws Exception{
		
		return dAOHomeTenant.save(tenant);

	}
	
	@Transactional
	public void editTenant(EntityTenant tenant) {
		
		dAOHomeTenant.editTenant(tenant);
		
	}
	

	@Transactional
	public Boolean editTenantUrl(EntityTenant tenant) throws SQLException {
		return dAOHomeTenant.editTenantUrl(tenant);
		
	}
	
	@Transactional
	public ResponseTenantHome findAllTenants(Integer itemsPerPage, Integer startIndex) {
		
		ResponseTenantHome responseTenants = new ResponseTenantHome();
		
		List<EntityTenant> tenants = dAOHomeTenant.findAllTenants(itemsPerPage, startIndex);
		
		responseTenants.setTenants(tenants);
					
		if(startIndex == 0) {
			Long countResults = dAOHomeTenant.countOfAllTenants();
			responseTenants.setCountOfTenants(countResults);
		}	
		
		return responseTenants;
	}

	@Transactional
	public EntityTenant findById(Integer tenantId) {
		
		return dAOHomeTenant.findById(tenantId);

	}
	
	@Transactional
	public EntityTenant findByTenantUrl(String tenantUrl) {

		return dAOHomeTenant.findByTenantUrl(tenantUrl);
	}

	@Transactional
	public EntityTenant checkTenantActiveStatus(String tenantUrl) {
		
		return dAOHomeTenant.checkTenantActiveStatus(tenantUrl);
	}
	
	
	@Transactional
	public EntityTenant checkTenantActiveStatusFromId(Integer tenantId) {

		return dAOHomeTenant.checkTenantActiveStatusFromId(tenantId);
	}

	@Transactional
	public List<EntityTenant> fetchStoresInLocation() {
		
		return dAOHomeTenant.fetchStoresInLocation();
	}

	@Transactional
	public ResponseTenantsHome searchTenant(String search, String tenantCity, String tenantArea, Integer itemsPerPage,
			Integer startIndex) {
	       FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
	        
	        try {
				fullTextEntityManager.createIndexer().startAndWait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(EntityTenant.class).get(); 
	        
	        List<EntityTenant> dbTenants = new ArrayList<EntityTenant>();
	        ResponseTenantsHome responseTenants = new ResponseTenantsHome();
	        

        	Query luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1).onFields("tenantName")
                .matching(search).createQuery();
        	
        	javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, EntityTenant.class);
        	
    		jpaQuery.setFirstResult(startIndex);
    		jpaQuery.setMaxResults(itemsPerPage);
    		
            try {
                dbTenants = jpaQuery.getResultList();
            } catch (NoResultException nre) {
                ;// do nothing
            }
     		if(startIndex == 0) {
     			Integer countResults = ((FullTextQuery) jpaQuery).getResultSize();
     			responseTenants.setCountOfTenants( Long.valueOf(countResults));
     		}

	        // remove soft deleted customers and irrelevant customer types 
	        Iterator itr = dbTenants.iterator();
	        
	 		responseTenants.setTenants(dbTenants);

	        return responseTenants;
	}

	@Transactional
	public Boolean resetTenantEmployeePassword(DTOEmployeeTenantPasswordReset dTOPasswordReset) throws SQLException {
		return dAOHomeTenant.resetTenantEmployeePassword(dTOPasswordReset);
		
	}

	
   @Transactional
   public ResponseTenantHome searchTenantByName(String search, Integer itemsPerPage, Integer startIndex) { 

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        
        try {
			fullTextEntityManager.createIndexer().startAndWait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(EntityTenant.class).get(); 
        
        List<EntityTenant> dbTenants = new ArrayList<EntityTenant>();
        ResponseTenantHome responseTenants = new ResponseTenantHome();
    
    	Query luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1).onFields("tenantName")
            .matching(search).createQuery();
    	
    	javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, EntityTenant.class);
    	
		jpaQuery.setFirstResult(startIndex);
		jpaQuery.setMaxResults(itemsPerPage);
		
        try {
            dbTenants = jpaQuery.getResultList();
        } catch (NoResultException nre) {
            ;// do nothing
        }
 		if(startIndex == 0) {
 			Integer countResults = ((FullTextQuery) jpaQuery).getResultSize();
 			responseTenants.setCountOfTenants( Long.valueOf(countResults));
 		}

 		responseTenants.setTenants(dbTenants);

        return responseTenants;
    }
   
   
 
	public ShopImageUrl saveShopImage(MultipartFile file) {
		
		ShopImageUrl shopImageUrl = new ShopImageUrl();
		
	
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
     
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        //    return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        } 
	        
	    String fileDownloadUri = "http://localhost:5000/api/shop-images/" + fileName ;
	        
		
		shopImageUrl.setShopImgUrl(fileDownloadUri);
		return shopImageUrl;
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
	

	
}
