package springboot.adminTenant.batches;

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
public class EntityProductBatches {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="product_id")
	private Integer productId;
	
	@Column(name="product_name")
	private String productName;
    
    //@Column(name = "sku")
    //private String sku;
    
    //@Column(name = "product_delete_status")
    //private Boolean productDeleteStatus;

    @Column(name = "hsn_code")
    private String hsnCode;
    
    @Column(name = "gst")
    private Integer gst;
  

	
	@ManyToOne(cascade= 
	{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name="product_brand_id")
	private EntityBrandBatches brand;
	
	@OneToMany(mappedBy="product", cascade= 
	{CascadeType.DETACH, CascadeType.MERGE, 
		CascadeType.PERSIST, CascadeType.REFRESH})
	private List<EntityProductVariantByUnitBatches> productVariantsByUnit;
	
	public EntityProductBatches () {
		
	}

	public EntityProductBatches(String productName, String hsnCode, Integer gst, EntityBrandBatches brand,
			List<EntityProductVariantByUnitBatches> productVariantsByUnit) {
		super();
		this.productName = productName;
		this.hsnCode = hsnCode;
		this.gst = gst;
		this.brand = brand;
		this.productVariantsByUnit = productVariantsByUnit;
	}

	@Override
	public String toString() {
		return "EntityProductBatches [productId=" + productId + ", productName=" + productName + ", hsnCode=" + hsnCode
				+ ", gst=" + gst + ", brand=" + brand + ", productVariantsByUnit=" + productVariantsByUnit + "]";
	}

}
