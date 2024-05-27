package springboot.admin.homeCompany.brands;

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
import javax.persistence.OneToMany;
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




@Getter
@Setter
@Entity
@Indexed
@Table(name="products")
public class EntityProductBrandHome {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="product_id")
	private Integer productId;
	
	@Column(name="product_name")
	@Field(termVector = TermVector.YES)
	private String productName;	

    
	@ManyToOne(cascade= 
	{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name="product_brand_id")
	private EntityBrandHome brand;
   

	public EntityProductBrandHome() {
		
	}


	@Override
	public String toString() {
		return "EntityProductBrand [productId=" + productId + ", productName=" + productName + ", brand=" + brand + "]";
	}


	public EntityProductBrandHome(String productName, EntityBrandHome brand) {
		super();
		this.productName = productName;
		this.brand = brand;
	}




	
}
