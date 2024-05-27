package springboot.admin.homeCompany.variants;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="images")
public class EntityImageVariantHome {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="image_id")
	private Integer imageId;
	
	@Column(name="image_name")
	private String imageName;
	
	@Column(name="image_url")
	//@Lob
//	private Resource imageUrl;
//	private MultipartFile image;
	private String imageUrl;
	
	
	
	@JsonIgnore
	@ManyToMany(fetch=FetchType.EAGER, cascade= 
				{CascadeType.DETACH, CascadeType.MERGE,CascadeType.PERSIST, CascadeType.REFRESH}, 
				mappedBy="images")	
	private List<EntityProductVariantByUnitHome> variants;
	
	//define constructors
	public EntityImageVariantHome() {		
	}

	@Override
	public String toString() {
		return "EntityImageProductHome [imageId=" + imageId + ", imageName=" + imageName + ", imageUrl=" + imageUrl
				+ ", variants=" + variants + "]";
	}

	public EntityImageVariantHome(String imageName, String imageUrl, List<EntityProductVariantByUnitHome> variants) {
		super();
		this.imageName = imageName;
		this.imageUrl = imageUrl;
		this.variants = variants;
	}
	

}	