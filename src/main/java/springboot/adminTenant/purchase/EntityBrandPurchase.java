 package springboot.adminTenant.purchase;

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
public class EntityBrandPurchase {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="brand_id")
	private int brandId;
	
	@Column(name="brand_name")
	private String brandName;
	
	@JsonIgnore
	@OneToMany(mappedBy="brand", cascade= 
	{CascadeType.DETACH, CascadeType.MERGE, 
		CascadeType.PERSIST, CascadeType.REFRESH})
	private List<EntityProductPurchase> products;
	
	//define constructors
	public EntityBrandPurchase() {		
	}

	public EntityBrandPurchase(String brandName, List<EntityProductPurchase> products) {
		this.brandName = brandName;
		this.products = products;
	}

	//define getters and setters
	
}
