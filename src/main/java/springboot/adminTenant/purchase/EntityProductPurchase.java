package springboot.adminTenant.purchase;

import java.util.Date;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.TermVector;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="products")
public class EntityProductPurchase {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="product_id")
	private Integer productId;
	
	@Column(name="product_name")
	private String productName;

    @Column(name = "hsn_code")
    private String hsnCode;
    
    @Column(name = "gst")
    private Integer gst;
    
   
	@OneToMany(mappedBy="product", cascade= 
	{CascadeType.DETACH, CascadeType.MERGE, 
		CascadeType.PERSIST, CascadeType.REFRESH})
	private List<EntityProductVariantByUnitPurchase> productVariantsByUnit;
	
	@ManyToOne(cascade= 
	{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name="product_brand_id")
	private EntityBrandPurchase brand;

	@Override
	public String toString() {
		return "EntityProductPurchase [productId=" + productId + ", productName=" + productName + ", hsnCode=" + hsnCode
				+ ", gst=" + gst + ", productVariantsByUnit=" + productVariantsByUnit + ", brand=" + brand + "]";
	}
	
	public EntityProductPurchase() {
		
	}

	public EntityProductPurchase(String productName, String hsnCode, Integer gst,
			List<EntityProductVariantByUnitPurchase> productVariantsByUnit, EntityBrandPurchase brand) {
		super();
		this.productName = productName;
		this.hsnCode = hsnCode;
		this.gst = gst;
		this.productVariantsByUnit = productVariantsByUnit;
		this.brand = brand;
	}


}
