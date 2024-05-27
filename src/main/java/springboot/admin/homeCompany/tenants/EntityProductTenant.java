package springboot.admin.homeCompany.tenants;

import java.sql.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;


import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.TermVector;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import springboot.admin.homeCompany.brands.EntityBrandHome;




@Getter
@Setter
@Entity
@Indexed
@Table(name="products")
public class EntityProductTenant {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="product_id")
	private Integer productId;
	
	@Column(name="product_name")
	@Field(termVector = TermVector.YES)
	private String productName;	
	
	@Column(name="bulk_list")
	private Boolean bulkList;	
     
	@ManyToOne(cascade= 
	{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name="product_brand_id")
	private EntityBrandHome brand;

	public EntityProductTenant() {
		
	}

	@Override
	public String toString() {
		return "EntityProductTenant [productId=" + productId + ", productName=" + productName + ", bulkList=" + bulkList
				+ ", brand=" + brand + "]";
	}

	public EntityProductTenant(String productName, Boolean bulkList, EntityBrandHome brand) {
		super();
		this.productName = productName;
		this.bulkList = bulkList;
		this.brand = brand;
	}
	
}
