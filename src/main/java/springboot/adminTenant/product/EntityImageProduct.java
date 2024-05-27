package springboot.adminTenant.product;

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
public class EntityImageProduct {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="image_id")
	private Integer imageId;
	
	@Column(name="image_home_id")
	private Integer imageHomeId;
	
	@Column(name="image_name")
	private String imageName;
	
	@Column(name="image_url")
	//@Lob
//	private Resource imageUrl;
//	private MultipartFile image;
	private String imageUrl;
	
	@JsonIgnore
	@ManyToMany(fetch=FetchType.EAGER, cascade= 
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(
			name="product_variant_by_unit_image",
			joinColumns=@JoinColumn(name="image_id"),
			inverseJoinColumns=@JoinColumn(name="variant_id")		
			)
	private List<EntityProductVariantByUnitProduct> variants;
	
	//define constructors
	public EntityImageProduct() {		
	}

	@Override
	public String toString() {
		return "EntityImageProduct [imageId=" + imageId + ", imageHomeId=" + imageHomeId + ", imageName=" + imageName
				+ ", imageUrl=" + imageUrl + "]";
	}

	public EntityImageProduct(Integer imageHomeId, String imageName, String imageUrl,
			List<EntityProductVariantByUnitProduct> variants) {
		super();
		this.imageHomeId = imageHomeId;
		this.imageName = imageName;
		this.imageUrl = imageUrl;
		this.variants = variants;
	}
	
}	