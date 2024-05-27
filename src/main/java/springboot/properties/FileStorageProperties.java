package springboot.properties;


	
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
	private String uploadDir;
	private String uploadShopPhotosDir;

	public String getUploadDir() {
		return uploadDir;
	}

	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}
	
	public String getUploadShopPhotosDir() {
		return uploadShopPhotosDir;
	}

	public void setUploadShopPhotosDir(String uploadShopPhotosDir) {
		this.uploadShopPhotosDir = uploadShopPhotosDir;
	}
	
	
	
}




