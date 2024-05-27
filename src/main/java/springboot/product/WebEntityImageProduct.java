package springboot.product;

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
public class WebEntityImageProduct {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="image_id")
	private int imageId;
	
	@Column(name="image_home_id")
	private int imageHomeId;
	
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
	private List<WebEntityProductVariantByUnit> variants;
	
	//define constructors
	public WebEntityImageProduct() {		
	}

	@Override
	public String toString() {
		return "WebEntityImageProduct [imageId=" + imageId + ", imageHomeId=" + imageHomeId + ", imageName=" + imageName
				+ ", imageUrl=" + imageUrl + ", variants=" + variants + "]";
	}

	public WebEntityImageProduct(int imageHomeId, String imageName, String imageUrl,
			List<WebEntityProductVariantByUnit> variants) {
		super();
		this.imageHomeId = imageHomeId;
		this.imageName = imageName;
		this.imageUrl = imageUrl;
		this.variants = variants;
	}


	
}	