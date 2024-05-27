package springboot.adminTenant.product;

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
public class EntityProduct {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="product_id")
	private Integer productId;
	
	@Column(name="product_home_id")
	private Integer productHomeId;	
	
	@Column(name="product_name")
	@Field(termVector = TermVector.YES)
	private String productName;	
  
    @Column(name = "gst")
    private Integer gst;    

  // @Column(name = "product_delete_status")
  // private Boolean productDeleteStatus;
		
    @Column(name = "description")
    private String description;  
    

  //@Column(name = "quantity")
  //private Float quantity; 
	
  
	
  //@Column(name="product_MRP")
  //private Float productMrp;
	
	//@Column(name="kg_pieces_per_unit")
	//private Float kgPiecesPerUnit;
	
	//@Column(name="kg_pieces_name")
	//private String kgPiecesName;
	
	@Column(name="has_expiry_date")
	private Boolean hasExpiryDate;
	
    @Column(name = "date_created")
    @CreationTimestamp
    private Date dateCreated;

    @Column(name = "last_updated")
    @UpdateTimestamp
    private Date lastUpdated;
	
    @Column(name = "hsn_code")
    private String hsnCode;
    
    @Column(name = "sort_order")
    private Integer sortOrder;
    
    @Column(name = "meta_title")
    private String metaTitle;
    
    @Column(name = "meta_description")
    private String metaDescription;
    
    @Column(name = "meta_keywords")
    private String metaKeywords;
    
    @Column(name = "seo_url")
    private String seoURL;
    
    //@Column(name= "images_order")
    //private String imagesOrder;
    
	@ManyToOne(cascade= 
	{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name="product_brand_id")
	private EntityBrandProduct brand;
    
	@ManyToMany(fetch=FetchType.LAZY, cascade= 
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(
			name="product_category",
			joinColumns=@JoinColumn(name="product_id"),
			inverseJoinColumns=@JoinColumn(name="category_id")		
			)
	private List<EntityCategoryProduct> categories;
	
	@OneToMany(mappedBy="product", cascade= 
	{CascadeType.DETACH, CascadeType.MERGE, 
		CascadeType.PERSIST, CascadeType.REFRESH})
	private List<EntityProductVariantByUnitProduct> productVariantsByUnit;
	

	public EntityProduct() {
		
	}


	@Override
	public String toString() {
		return "EntityProduct [productId=" + productId + ", productHomeId=" + productHomeId
				+ ", productName=" + productName + ", gst=" + gst + ", description=" + description + ", hasExpiryDate="
				+ hasExpiryDate + ", dateCreated=" + dateCreated + ", lastUpdated=" + lastUpdated + ", hsnCode="
				+ hsnCode + ", sortOrder=" + sortOrder + ", metaTitle=" + metaTitle + ", metaDescription="
				+ metaDescription + ", metaKeywords=" + metaKeywords + ", seoURL=" + seoURL + ", brand=" + brand
				+ ", categories=" + categories + ", productVariantsByUnit=" + productVariantsByUnit + "]";
	}


	public EntityProduct(Integer productHomeId, String productName, Integer gst, String description,
			Boolean hasExpiryDate, Date dateCreated, Date lastUpdated, String hsnCode, Integer sortOrder,
			String metaTitle, String metaDescription, String metaKeywords, String seoURL, EntityBrandProduct brand,
			List<EntityCategoryProduct> categories, List<EntityProductVariantByUnitProduct> productVariantsByUnit) {
		super();
		this.productHomeId = productHomeId;
		this.productName = productName;
		this.gst = gst;
		this.description = description;
		this.hasExpiryDate = hasExpiryDate;
		this.dateCreated = dateCreated;
		this.lastUpdated = lastUpdated;
		this.hsnCode = hsnCode;
		this.sortOrder = sortOrder;
		this.metaTitle = metaTitle;
		this.metaDescription = metaDescription;
		this.metaKeywords = metaKeywords;
		this.seoURL = seoURL;
		this.brand = brand;
		this.categories = categories;
		this.productVariantsByUnit = productVariantsByUnit;
	}




}
