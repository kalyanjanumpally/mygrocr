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
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="brands")
public class EntityBrandProduct {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="brand_id")
	private Integer brandId;
	
	@Column(name="brand_home_id")
	private Integer brandHomeId;
	
	@Column(name="brand_name")
	private String brandName;
	
	@JsonIgnore
	@OneToMany(mappedBy="brand", cascade= 
	{CascadeType.DETACH, CascadeType.MERGE, 
		CascadeType.PERSIST, CascadeType.REFRESH})
	private List<EntityProduct> products;
	
	//define constructors
	public EntityBrandProduct() {		
	}

	public EntityBrandProduct(Integer brandHomeId, String brandName, List<EntityProduct> products) {
		super();
		this.brandHomeId = brandHomeId;
		this.brandName = brandName;
		this.products = products;
	}

	@Override
	public String toString() {
		return "EntityBrandProduct [brandHomeId=" + brandHomeId + ", brandName=" + brandName + "]";
	}
	
}
